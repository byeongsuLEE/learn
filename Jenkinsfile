pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-login')
        DOCKER_REGISTRY = 'evil55'
        COMPOSE_FILE = '/home/lbs/docker-compose.yml'
        DISCORD_URL = credentials('discord-webhook-url')
    }

    stages {
        stage('Git Clone') {
            steps {
                echo '📦 GitHub에서 learn 코드 가져오는 중...'
                script {
                    git branch: 'master',
                        url: 'https://github.com/byeongsuLEE/learn.git',
                        credentialsId: 'github-access-Token'
                }
                sh 'ls -la'
                echo '✅ GitHub 연결 성공!'
            }
        }
// build test
//         //  GCP 키 파일 준비
//         stage('Prepare GCP Credentials') {
//             steps {
//                 echo '🔑 GCP 자격 증명 파일 준비 중...'
//                 withCredentials([file(credentialsId: 'GCPStorageKey', variable: 'GCP_KEY_FILE_PATH')]) {
//                     sh """
//                         # GCP 키를 복사할 디렉토리 생성
//                         mkdir -p UserService/src/main/resources
//
//                         # Jenkins가 임시로 제공한 키 파일을 빌드 디렉토리로 복사
//                         cp ${GCP_KEY_FILE_PATH} UserService/src/main/resources/gcp-key.json
//
//                         echo "✅ GCP 자격 증명 파일 준비 완료!"
//                     """
//                 }
//             }
//         }
// nginix를 이용한 webhook 성공으로 인한 ufw github ip 제거

        stage('Detect Changed Services') {
            steps {
                script {
                    echo '🔍 변경된 서비스 감지 중...'

                    def changedServices = []
                    def serviceMap = [
                        'UserService': 'user',
                        'GatewayService': 'gateway'
                    ]

                    // Git diff로 변경된 파일들 확인
                    def changes = []
                    try {
                        // 더 안정적인 방법으로 변경 감지
                        def gitLog = sh(
                            script: "git log --oneline -1",
                            returnStdout: true
                        ).trim()
                        echo "최근 커밋: ${gitLog}"

                        // HEAD~1이 없을 수 있으므로 안전하게 처리
                        def previousCommit = sh(
                            script: "git rev-parse HEAD~1 2>/dev/null || echo 'NONE'",
                            returnStdout: true
                        ).trim()

                        if (previousCommit == 'NONE') {
                            echo "이전 커밋이 없습니다. 전체 서비스를 확인합니다."
                            // 모든 서비스 폴더 확인
                            serviceMap.each { folder, dockerService ->
                                if (fileExists(folder)) {
                                    changedServices.add(folder)
                                    echo "✅ ${folder} 서비스 감지"
                                }
                            }
                        } else {
                            changes = sh(
                                script: "git diff --name-only HEAD~1 HEAD",
                                returnStdout: true
                            ).trim().split('\n')

                            echo "📋 변경된 파일들:"
                            changes.each { file ->
                                echo "  - ${file}"
                            }

                            // 변경된 파일에 따라 서비스 감지
                            serviceMap.each { folder, dockerService ->
                                def hasServiceChanges = changes.any { it.startsWith("${folder}/") }
                                if (hasServiceChanges) {
                                    changedServices.add(folder)
                                    echo "✅ ${folder} 서비스 변경 감지"
                                }
                            }
                        }
                    } catch (Exception e) {
                        echo "Git 변경 감지 중 오류 발생: ${e.message}"
                        echo "기본값으로 UserService를 배포합니다."
                        changedServices.add('UserService')
                    }

                    // 변경사항이 없을 때 처리
                    if (changedServices.isEmpty()) {
                        echo "✅ 변경된 서비스가 없습니다. 배포를 건너뜁니다."
                        echo "📋 확인된 서비스 폴더: ${serviceMap.keySet()}"
                    }

                    // 환경 변수 설정을 더 명확하게
                    def servicesString = changedServices.join(',')

                    env.CHANGED_SERVICES = servicesString
                    echo "servicesString 값: ${servicesString}"
                    echo "🎯 배포할 서비스: ${env.CHANGED_SERVICES}"
                    echo "🔍 디버그 - changedServices: ${changedServices}"
                    echo "🔍 디버그 - changedServices.size(): ${changedServices.size()}"
                    echo "🔍 디버그 - env.CHANGED_SERVICES: ${env.CHANGED_SERVICES}"
                }
            }
        }

        stage('UserService Deploy') {
            when {
                expression {
                    return env.CHANGED_SERVICES?.contains('UserService')
                }
            }
            stages {
                stage('UserService Build') {
                    steps {
                        dir('UserService') {
                            echo '🔨 UserService Gradle 빌드 시작...'
                            script {
                                // withCredentials 블록을 이 단계에만 유지
                                withCredentials([file(credentialsId: 'GCPStorageKey', variable: 'GCP_KEY_FILE')]) {
                                    // 이 부분은 그대로 유지
                                    sh '''
                                          chmod +x gradlew
                                          # GCP 키 파일을 안정적인 임시 위치로 복사합니다.
                                          mkdir -p /tmp/jenkins-credentials
                                          cp ${GCP_KEY_FILE} /tmp/jenkins-credentials/gcp-key.json

                                          # 빌드 및 테스트를 실행하며, -D 옵션으로 안정적인 경로를 전달합니다.
                                          ./gradlew clean bootJar -x test -x asciidoctor -Dspring.profiles.active=prod -Dgoogle.cloud.storage.credentials.location=/tmp/jenkins-credentials/gcp-key.json
                                          echo "빌드된 JAR 파일 확인:"
                                        ls -la build/libs/
                                    '''

                                }
                            }
                            echo '✅ UserService 빌드 완료!'
                        }
                    }
                }

                stage('UserService Docker Build & Push') {
                    steps {
                        dir('UserService') {
                            script {
                                def imageTag = "${env.BUILD_NUMBER}"
                                def imageName = "${DOCKER_REGISTRY}/user:${imageTag}"
                                def latestImageName = "${DOCKER_REGISTRY}/user:latest"

                                echo "🐳 UserService Docker 이미지 빌드: ${imageName}"

                                sh "docker build -t ${imageName} -t ${latestImageName} ."

                                sh '''
                                    echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin
                                '''
                                sh "docker push ${imageName}"
                                sh "docker push ${latestImageName}"

                                echo '✅ UserService Docker Hub 푸시 완료!'
                            }
                        }
                    }
                }

                stage('UserService Deploy') {
                    steps {
                        script {
                            echo '🚀 UserService 배포 시작...'
                            sh """
                                # UserService 컨테이너 중지
                                docker compose -f ${COMPOSE_FILE} stop user || true

                                # 기존 컨테이너 제거
                                docker compose -f ${COMPOSE_FILE} rm -f user || true

                                # 기존 이미지 제거
                                docker rmi ${DOCKER_REGISTRY}/user:latest || true

                                # 새 이미지 pull
                                docker pull ${DOCKER_REGISTRY}/user:latest

                                docker compose -f ${COMPOSE_FILE} up -d user

                                # 컨테이너 상태 확인
                                sleep 10

                                docker compose -f ${COMPOSE_FILE} ps user
                            """
                            echo '✅ UserService 배포 완료!'
                        }
                    }
                }
            }
        }

        stage('Gateway Deploy') {
            when {
                expression {
                    return env.CHANGED_SERVICES?.contains('GatewayService')
                }
            }
            stages {
                stage('Gateway Build') {
                    steps {
                        dir('GatewayService') {
                            echo '🔨 Gateway Gradle 빌드 시작...'
                            sh '''
                                chmod +x gradlew
                                ./gradlew clean build
                                echo "빌드된 JAR 파일 확인:"
                                ls -la build/libs/
                            '''
                            echo '✅ Gateway 빌드 완료!'
                        }
                    }
                }

                stage('Gateway Docker Build & Push') {
                    steps {
                        dir('GatewayService') {
                            script {
                                def imageTag = "${env.BUILD_NUMBER}"
                                def imageName = "${DOCKER_REGISTRY}/gateway:${imageTag}"
                                def latestImageName = "${DOCKER_REGISTRY}/gateway:latest"

                                echo "🐳 Gateway Docker 이미지 빌드: ${imageName}"

                                sh "docker build -t ${imageName} -t ${latestImageName} ."

                                sh '''
                                    echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin
                                '''
                                sh "docker push ${imageName}"
                                sh "docker push ${latestImageName}"

                                echo '✅ Gateway Docker Hub 푸시 완료!'
                            }
                        }
                    }
                }

                stage('Gateway Deploy') {
                    steps {
                        script {
                            echo '🚀 Gateway 배포 시작...'
                            sh """
                                # Gateway 컨테이너 중지
                                docker compose -f ${COMPOSE_FILE} stop gateway || true

                                # 기존 컨테이너 제거
                                docker compose -f ${COMPOSE_FILE} rm -f gateway || true

                                # 기존 이미지 제거
                                docker rmi ${DOCKER_REGISTRY}/gateway:latest || true

                                # 새 이미지 pull
                                docker pull ${DOCKER_REGISTRY}/gateway:latest

                                # Gateway 컨테이너 시작
                                docker compose -f ${COMPOSE_FILE} up -d gateway

                                # 컨테이너 상태 확인
                                sleep 10
                                docker compose -f ${COMPOSE_FILE} ps gateway
                            """
                            echo '✅ Gateway 배포 완료!'
                        }
                    }
                }
            }
        }

        // 헬스체크 스테이지 - 배포된 서비스가 있을 때만 실행
        stage('Health Check') {
            when {
                expression {
                    return env.CHANGED_SERVICES != null && env.CHANGED_SERVICES.trim() != ''
                }
            }
            steps {
                script {
                    echo '🏥 배포된 서비스 헬스체크 시작...'
                    echo "헬스체크할 서비스: ${env.CHANGED_SERVICES}"

                    def services = env.CHANGED_SERVICES.split(',')
                    def serviceHealthMap = [
                        'UserService': 'http://evil55.cloud/api/user-service/actuator/health',
                        'GatewayService': 'http://evil55.cloud:8000/actuator/health'
                    ]

                    def failedServices = []

                    services.each { service ->
                        def healthUrl = serviceHealthMap[service]
                        if (healthUrl) {
                            echo "🔍 ${service} 헬스체크 중... (URL: ${healthUrl})"

                            def maxAttempts = 20
                            def currentAttempt = 0
                            def isHealthy = false

                            while (currentAttempt < maxAttempts && !isHealthy) {
                                currentAttempt++
                                echo "📋 ${service} 헬스체크 시도 ${currentAttempt}/${maxAttempts}"

                                try {
                                    def response = sh(
                                        script: "curl -s -L -o /dev/null -w '%{http_code}' ${healthUrl}",
                                        returnStdout: true
                                    ).trim()

                                    if (response == '200') {
                                        echo "✅ ${service} 헬스체크 성공! (${currentAttempt}번째 시도)"
                                        isHealthy = true
                                    } else {
                                        echo "⏳ ${service} 헬스체크 실패 (응답코드: ${response})"
                                        if (currentAttempt < maxAttempts) {
                                            echo "⏳ 10초 후 재시도..."
                                            sleep(10)
                                        }
                                    }
                                } catch (Exception e) {
                                    echo "⏳ ${service} 헬스체크 실패 (연결 실패: ${e.message})"
                                    if (currentAttempt < maxAttempts) {
                                        echo "⏳ 10초 후 재시도..."
                                        sleep(10)
                                    }
                                }
                            }

                            if (!isHealthy) {
                                failedServices.add(service)
                                echo "❌ ${service} 헬스체크 최종 실패 (${maxAttempts}번 시도 후 포기)"

                                // Docker 이미지 정리
                                cleanupFailedService(service)
                            }
                        }
                    }

                    // 실패한 서비스가 있으면 빌드 전체 실패
                    if (!failedServices.isEmpty()) {
                        def failedServicesString = failedServices.join(', ')
                        echo "💥 헬스체크 실패한 서비스: ${failedServicesString}"
                        error("헬스체크 실패로 인한 배포 중단: ${failedServicesString}")
                    }
                }
            }
        }

        stage('Service Status Check') {
            when {
                expression {
                    return env.CHANGED_SERVICES != null && env.CHANGED_SERVICES.trim() != ''
                }
            }
            steps {
                script {
                    echo '📊 서비스 상태 확인...'
                    echo "확인할 서비스: ${env.CHANGED_SERVICES}"

                    sh """
                        echo "=== 전체 Docker Compose 서비스 상태 ==="
                        docker compose -f ${COMPOSE_FILE} ps
                        echo ""
                    """

                    def services = env.CHANGED_SERVICES.split(',')
                    def serviceMap = [
                        'UserService': 'user',
                        'GatewayService': 'gateway'
                    ]

                    services.each { service ->
                        def dockerService = serviceMap[service]
                        if (dockerService) {
                            sh """
                                echo "=== ${service} (${dockerService}) 상세 정보 ==="
                                docker compose -f ${COMPOSE_FILE} ps ${dockerService}
                                echo ""
                                echo "--- ${service} 최근 로그 (20줄) ---"
                                docker compose -f ${COMPOSE_FILE} logs --tail=20 ${dockerService} || true
                                echo ""
                            """
                        }
                    }
                }
            }
        }

        stage('Docker Image Cleanup') {
            steps {
                sh '''
                    echo "🧹 사용하지 않는 Docker 이미지 정리 중..."
                    docker image prune -f

                    echo "=== 현재 evil55 이미지 목록 ==="
                    docker images | grep evil55 || echo "evil55 이미지가 없습니다."
                '''
                echo '✅ Docker 이미지 정리 완료!'
            }
        }
    }

    post {
        success {
            script {
                def deployedServices = env.CHANGED_SERVICES ?: ''

                // 디스코드 알림을 위한 변수 설정
                def discordMsg = ""

                if (deployedServices.trim() == '') {
                    echo '🎉 파이프라인 완료! (배포된 서비스 없음)'
                    echo '✨ 변경사항이 없어 배포를 건너뛰었습니다.'
                    echo "📋 빌드 번호: ${env.BUILD_NUMBER}"
                    echo "📋 완료 시간: ${new Date()}"

                    discordMsg = "✨ 변경사항이 없어 배포를 건너뛰었습니다."
                } else {
                    echo '🎉 서비스 배포 파이프라인 성공!'
                    echo "✅ 배포된 서비스: ${deployedServices}"
                    echo "🌐 서비스 접속 URL:"

                    discordMsg = "✅ 배포된 서비스: ${deployedServices}\n🌐 https://evil55.cloud"

                    def services = deployedServices.split(',')
                    services.each { service ->
                        if (service == 'UserService') {
                            echo "  - UserService API: https://evil55.cloud/api/user-service"
                            echo "  - UserService Health: http://evil55.cloud:8081/actuator/health"
                        } else if (service == 'GatewayService') {
                            echo "  - Gateway: https://evil55.cloud"
                            echo "  - Gateway Health: http://evil55.cloud:8000/actuator/health"
                        }
                    }

                    echo "📋 배포 정보:"
                    echo "  - 빌드 번호: ${env.BUILD_NUMBER}"
                    echo "  - 배포 시간: ${new Date()}"
                }

                // 성공 알림 전송 (Discord)
                discordSend(
                    webhookURL: "${env.DISCORD_URL}",
                    title: "🚀 배포 성공: Build #${env.BUILD_NUMBER}",
                    link: env.RUN_DISPLAY_URL,
                    description: discordMsg,
                    result: 'SUCCESS'
                )
            }
        }
        failure {
            script {
                echo '❌ 서비스 배포 파이프라인 실패!!'
                echo "❌ 실패한 서비스: ${env.CHANGED_SERVICES ?: 'none'}"

                // 실패 알림 전송 (Discord)
                discordSend(
                    webhookURL: "${env.DISCORD_URL}",
                    title: "❌ 배포 실패: Build #${env.BUILD_NUMBER}",
                    link: env.RUN_DISPLAY_URL,
                    description: "🚨 실패 서비스: ${env.CHANGED_SERVICES ?: '확인 필요'}\n📍 로그 확인을 위해 제목 링크를 클릭하세요.",
                    result: 'FAILURE'
                )

                sh '''
                    echo "=== 실패 시점 전체 컨테이너 상태 ==="
                    docker ps -a

                    echo "=== Docker Compose 상태 ==="
                    docker compose -f /home/lbs/docker-compose.yml ps

                    echo "=== 최근 시스템 로그 ==="
                    docker system df
                '''
            }
        }
        always {
            sh '''
                docker logout || true
            '''
            echo '🧹 파이프라인 정리 작업 완료'
        }
    }
}

// 실패한 서비스의 Docker 이미지 정리 함수
def cleanupFailedService(String serviceName) {
    try {
        echo "🗑️ ${serviceName} 실패한 컨테이너 및 이미지 정리 중..."

        def serviceMap = [
            'UserService': 'user',
            'GatewayService': 'gateway'
        ]

        def dockerService = serviceMap[serviceName]
        if (dockerService) {
            sh """
                # 실패한 컨테이너 중지 및 제거
                docker compose -f ${COMPOSE_FILE} stop ${dockerService} || true
                docker compose -f ${COMPOSE_FILE} rm -f ${dockerService} || true

                # 실패한 이미지 제거 (현재 배포 시도한 이미지)
                docker rmi ${DOCKER_REGISTRY}/${dockerService}:latest || true
                docker rmi ${DOCKER_REGISTRY}/${dockerService}:${env.BUILD_NUMBER} || true

                # dangling 이미지 정리
                docker image prune -f
            """
        }

        echo "✅ ${serviceName} 정리 완료"
    } catch (Exception e) {
        echo "❌ ${serviceName} 정리 실패: ${e.message}"
    }
}