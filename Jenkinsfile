pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-login')
        DOCKER_REGISTRY = 'evil55'

        // 기존 docker-compose 파일 경로
        COMPOSE_FILE = '/home/lbs/docker-compose-back.yml'

        // 변경된 서비스들을 저장할 변수
        CHANGED_SERVICES = ''
    }

    stages {
        stage('Git Clone') {
            steps {
                echo '📦 GitHub에서 learn 코드 가져오는 중...'
                script {
                    try {
                        // 먼저 브랜치 정보 확인
                        sh '''
                            echo "=== 원격 브랜치 정보 확인 ==="
                            git ls-remote --heads https://github.com/byeongsuLEE/learn.git
                        '''

                        // 브랜치 이름 확인 후 적절한 브랜치로 변경
                        checkout([
                            $class: 'GitSCM',
                            branches: [[name: '*/main']], // main 브랜치 시도
                            userRemoteConfigs: [[
                                url: 'https://github.com/byeongsuLEE/learn.git',
                                credentialsId: 'github-access-Token'
                            ]]
                        ])
                    } catch (Exception e) {
                        echo "main 브랜치 실패, master 브랜치 시도 중..."
                        // main 실패 시 master 시도
                        checkout([
                            $class: 'GitSCM',
                            branches: [[name: '*/master']], // master 브랜치 시도
                            userRemoteConfigs: [[
                                url: 'https://github.com/byeongsuLEE/learn.git',
                                credentialsId: 'github-access-Token'
                            ]]
                        ])
                    }
                }
                sh 'ls -la'
                sh 'git branch -a'
                echo '✅ GitHub 연결 성공!'
            }
        }

        stage('Detect Changed Services') {
            steps {
                script {
                    echo '🔍 변경된 서비스 감지 중...'

                    def changedServices = []
                    def serviceMap = [
                        'UserService': 'user',      // 사용자 비즈니스 로직
                        'Gateway': 'gateway'        // API 게이트웨이
                    ]

                    // Git diff로 변경된 파일들 확인
                    def changes = []
                    try {
                        changes = sh(
                            script: "git diff --name-only HEAD~1 HEAD",
                            returnStdout: true
                        ).trim().split('\n')

                        echo "📋 변경된 파일들:"
                        changes.each { file ->
                            echo "  - ${file}"
                        }
                    } catch (Exception e) {
                        // 첫 번째 커밋인 경우 UserService만 배포
                        echo "첫 번째 커밋이거나 이전 커밋이 없습니다. UserService를 기본 배포합니다."
                        changedServices.add('UserService')
                    }

                    if (changes && changes.size() > 0 && !changedServices) {
                        // 개별 서비스 변경 감지만 수행 (Config Server가 설정 관리)
                        serviceMap.each { folder, dockerService ->
                            def hasServiceChanges = changes.any { it.startsWith("${folder}/") }
                            if (hasServiceChanges) {
                                changedServices.add(folder)
                                echo "✅ ${folder} 서비스 변경 감지"
                            }
                        }

                        if (!changedServices || changedServices.size() == 0) {
                            echo "📝 변경된 파일이 서비스 폴더 외부에 있습니다."
                            echo "📋 변경된 파일: ${changes.join(', ')}"
                            echo "⚠️ 서비스 배포가 필요한 경우 수동으로 트리거하세요."
                        }
                    }

                    // 변경된 서비스가 없으면 강제로 UserService 배포 (테스트용)
                    if (!changedServices || changedServices.size() == 0) {
                        echo "⚠️ 변경된 서비스가 없습니다. UserService를 기본 배포합니다."
                        changedServices = ['UserService']
                    }

                    // 환경 변수 설정 (확실한 값 설정)
                    def servicesString = changedServices.join(',')
                    env.CHANGED_SERVICES = servicesString

                    echo "🎯 배포할 서비스: ${env.CHANGED_SERVICES}"
                    echo "🔍 디버그 - changedServices: ${changedServices}"
                    echo "🔍 디버그 - servicesString: ${servicesString}"
                }
            }
        }

        stage('Build and Deploy Services') {
            parallel {
                stage('UserService Deploy') {
                    when {
                        expression {
                            env.CHANGED_SERVICES?.contains('UserService')
                        }
                    }
                    stages {
                        stage('UserService Build') {
                            steps {
                                dir('UserService') {
                                    echo '🔨 UserService Gradle 빌드 시작...'
                                    sh '''
                                        chmod +x gradlew
                                        ./gradlew clean build -x test
                                        echo "빌드된 JAR 파일 확인:"
                                        ls -la build/libs/
                                    '''
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

                                        // Docker 이미지 빌드
                                        sh "docker build -t ${imageName} -t ${latestImageName} ."

                                        // Docker Hub 로그인 및 푸시
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
                                        docker-compose -f ${COMPOSE_FILE} stop user || true

                                        # 기존 컨테이너 제거
                                        docker-compose -f ${COMPOSE_FILE} rm -f user || true

                                        # 기존 이미지 제거 (디스크 공간 확보)
                                        docker rmi ${DOCKER_REGISTRY}/user:latest || true

                                        # 새 이미지 pull
                                        docker pull ${DOCKER_REGISTRY}/user:latest

                                        # UserService 컨테이너 시작
                                        docker-compose -f ${COMPOSE_FILE} up -d user

                                        # 컨테이너 상태 확인
                                        sleep 10
                                        docker-compose -f ${COMPOSE_FILE} ps user
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
                            env.CHANGED_SERVICES?.contains('Gateway')
                        }
                    }
                    stages {
                        stage('Gateway Build') {
                            steps {
                                dir('Gateway') {
                                    echo '🔨 Gateway Gradle 빌드 시작...'
                                    sh '''
                                        chmod +x gradlew
                                        ./gradlew clean build -x test
                                        echo "빌드된 JAR 파일 확인:"
                                        ls -la build/libs/
                                    '''
                                    echo '✅ Gateway 빌드 완료!'
                                }
                            }
                        }

                        stage('Gateway Docker Build & Push') {
                            steps {
                                dir('Gateway') {
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
                                        docker-compose -f ${COMPOSE_FILE} stop gateway || true

                                        # 기존 컨테이너 제거
                                        docker-compose -f ${COMPOSE_FILE} rm -f gateway || true

                                        # 기존 이미지 제거
                                        docker rmi ${DOCKER_REGISTRY}/gateway:latest || true

                                        # 새 이미지 pull
                                        docker pull ${DOCKER_REGISTRY}/gateway:latest

                                        # Gateway 컨테이너 시작
                                        docker-compose -f ${COMPOSE_FILE} up -d gateway

                                        # 컨테이너 상태 확인
                                        sleep 10
                                        docker-compose -f ${COMPOSE_FILE} ps gateway
                                    """
                                    echo '✅ Gateway 배포 완료!'
                                }
                            }
                        }
                    }
                }
            }
        }

        stage('Health Check') {
            when {
                not {
                    anyOf {
                        environment name: 'CHANGED_SERVICES', value: ''
                        environment name: 'CHANGED_SERVICES', value: 'null'
                    }
                }
            }
            steps {
                script {
                    echo '🏥 배포된 서비스 헬스체크 시작...'
                    echo "🔍 CHANGED_SERVICES 값: '${env.CHANGED_SERVICES}'"

                    // 추가 안전 장치
                    if (!env.CHANGED_SERVICES || env.CHANGED_SERVICES == 'null' || env.CHANGED_SERVICES.trim() == '') {
                        echo '⚠️ 배포된 서비스가 없습니다. 헬스체크를 건너뜁니다.'
                        return
                    }

                    def services = env.CHANGED_SERVICES.split(',')
                    def serviceHealthMap = [
                        'UserService': 'http://evil55.shop:8081/actuator/health',
                        'Gateway': 'http://evil55.shop:8000/actuator/health'
                    ]

                    echo "🔍 헬스체크할 서비스: ${services.join(', ')}"

                    services.each { service ->
                        def healthUrl = serviceHealthMap[service]
                        if (healthUrl) {
                            echo "🔍 ${service} 헬스체크 중... (URL: ${healthUrl})"
                            timeout(time: 2, unit: 'MINUTES') {
                                waitUntil {
                                    script {
                                        try {
                                            def response = sh(
                                                script: "curl -s -o /dev/null -w '%{http_code}' ${healthUrl}",
                                                returnStdout: true
                                            ).trim()

                                            if (response == '200') {
                                                echo "✅ ${service} 헬스체크 성공!"
                                                return true
                                            } else {
                                                echo "⏳ ${service} 헬스체크 대기중... (응답코드: ${response})"
                                                sleep(10)
                                                return false
                                            }
                                        } catch (Exception e) {
                                            echo "⏳ ${service} 헬스체크 대기중... (연결 실패)"
                                            sleep(10)
                                            return false
                                        }
                                    }
                                }
                            }
                        } else {
                            echo "⚠️ ${service}에 대한 헬스체크 URL이 정의되지 않았습니다."
                        }
                    }
                }
            }
        }

        stage('Service Status Check') {
            steps {
                script {
                    echo '📊 배포된 서비스 상태 확인...'

                    def services = env.CHANGED_SERVICES.split(',')
                    def serviceMap = [
                        'UserService': 'user',
                        'Gateway': 'gateway'
                    ]

                    sh """
                        echo "=== 전체 Docker Compose 서비스 상태 ==="
                        docker-compose -f ${COMPOSE_FILE} ps
                        echo ""
                    """

                    services.each { service ->
                        def dockerService = serviceMap[service]
                        if (dockerService) {
                            sh """
                                echo "=== ${service} (${dockerService}) 상세 정보 ==="
                                docker-compose -f ${COMPOSE_FILE} ps ${dockerService}
                                echo ""
                                echo "--- ${service} 최근 로그 (20줄) ---"
                                docker-compose -f ${COMPOSE_FILE} logs --tail=20 ${dockerService} || true
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
                echo '🎉 서비스 배포 파이프라인 성공!'

                if (env.CHANGED_SERVICES) {
                    echo "✅ 배포된 서비스: ${env.CHANGED_SERVICES}"
                    echo "🌐 서비스 접속 URL:"

                    def services = env.CHANGED_SERVICES.split(',')
                    services.each { service ->
                        if (service == 'UserService') {
                            echo "  - UserService API: https://evil55.shop/api/user-service"
                            echo "  - UserService Health: http://evil55.shop:8081/actuator/health"
                        } else if (service == 'Gateway') {
                            echo "  - Gateway: https://evil55.shop"
                            echo "  - Gateway Health: http://evil55.shop:8000/actuator/health"
                        }
                    }
                } else {
                    echo "⚠️ 배포된 서비스가 없습니다."
                }

                echo "📋 배포 정보:"
                echo "  - 빌드 번호: ${env.BUILD_NUMBER}"
                echo "  - 배포 시간: ${new Date()}"
            }
        }
        failure {
            script {
                echo '❌ 서비스 배포 파이프라인 실패!!'

                if (env.CHANGED_SERVICES) {
                    echo "❌ 실패한 서비스: ${env.CHANGED_SERVICES}"
                } else {
                    echo "❌ 서비스 감지 또는 초기 설정에서 실패"
                }

                // 실패 시 디버깅 정보 수집
                sh '''
                    echo "=== 실패 시점 전체 컨테이너 상태 ==="
                    docker ps -a

                    echo "=== Docker Compose 상태 ==="
                    docker-compose -f /home/lbs/docker-compose-back.yml ps

                    echo "=== 최근 시스템 로그 ==="
                    docker system df
                '''
            }
        }
        always {
            // 빌드 후 정리 작업
            sh '''
                docker logout || true
            '''
            echo '🧹 파이프라인 정리 작업 완료'
        }
    }
}