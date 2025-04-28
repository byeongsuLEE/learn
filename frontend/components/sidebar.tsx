"use client"

import { useState } from "react"
import Link from "next/link"
import { usePathname } from "next/navigation"
import { BookOpen, Settings, BarChart, Users, Menu, X, LogOut } from "lucide-react"
import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"

interface SidebarProps {
  className?: string
}

export function Sidebar({ className }: SidebarProps) {
  const pathname = usePathname()
  const [isOpen, setIsOpen] = useState(false)

  const toggleSidebar = () => {
    setIsOpen(!isOpen)
  }

  const navItems = [
    // {
    //   name: "대시보드",
    //   href: "/dashboard",
    //   icon: Home,
    // },
    {
      name: "낱말카드",
      href: "/",
      icon: BookOpen,
    },
    {
      name: "통계",
      href: "/statistics",
      icon: BarChart,
    },
    {
      name: "친구",
      href: "/friends",
      icon: Users,
    },
    {
      name: "설정",
      href: "/settings",
      icon: Settings,
    },
  ]

  return (
    <>
      {/* 모바일 토글 버튼 */}
      <Button variant="outline" size="icon" className="fixed top-4 left-4 z-50 md:hidden" onClick={toggleSidebar}>
        {isOpen ? <X className="h-4 w-4" /> : <Menu className="h-4 w-4" />}
      </Button>

      {/* 사이드바 */}
      <div
        className={cn(
          "fixed inset-y-0 left-0 z-40 flex w-64 flex-col bg-white border-r shadow-sm transition-transform duration-300 md:translate-x-0",
          isOpen ? "translate-x-0" : "-translate-x-full",
          className,
        )}
      >
        <div className="flex h-16 items-center border-b px-6">
          <h2 className="text-lg font-semibold">학습 플랫폼</h2>
        </div>

        <div className="flex-1 overflow-auto py-4">
          <nav className="grid gap-1 px-2">
            {navItems.map((item) => (
              <Link
                key={item.href}
                href={item.href}
                className={cn(
                  "flex items-center gap-3 rounded-lg px-3 py-2 text-sm transition-colors",
                  pathname === item.href || (item.href !== "/dashboard" && pathname?.startsWith(item.href))
                    ? "bg-primary text-primary-foreground"
                    : "hover:bg-muted",
                )}
              >
                <item.icon className="h-4 w-4" />
                {item.name}
              </Link>
            ))}
          </nav>
        </div>

        <div className="mt-auto border-t p-4">
          <Button variant="outline" className="w-full justify-start" asChild>
            <Link href="/auth/logout" className="flex items-center gap-2">
              <LogOut className="h-4 w-4" />
              로그아웃
            </Link>
          </Button>
        </div>
      </div>
    </>
  )
}
