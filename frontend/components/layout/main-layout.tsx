import type { ReactNode } from "react"
import { Sidebar } from "./sidebar"

interface MainLayoutProps {
  children: ReactNode
}

export function MainLayout({ children }: MainLayoutProps) {
  return (
    <div className="flex min-h-screen">
      <Sidebar />
      <div className="flex-1 md:ml-64">
        <main className="container mx-auto p-4 md:p-6">{children}</main>
      </div>
    </div>
  )
}
