"use client"

import { useState, useEffect } from "react"

type ToastProps = {
  title?: string
  description?: string
  variant?: "default" | "destructive"
  duration?: number
}

export function toast(props: ToastProps) {
  const event = new CustomEvent("toast", { detail: props })
  window.dispatchEvent(event)
}

export function useToast() {
  const [toasts, setToasts] = useState<(ToastProps & { id: string })[]>([])

  useEffect(() => {
    const handleToast = (e: Event) => {
      const detail = (e as CustomEvent<ToastProps>).detail
      const id = Math.random().toString(36).substring(2, 9)
      setToasts((prev) => [...prev, { ...detail, id }])

      if (detail.duration !== Number.POSITIVE_INFINITY) {
        setTimeout(() => {
          setToasts((prev) => prev.filter((toast) => toast.id !== id))
        }, detail.duration || 3000)
      }
    }

    window.addEventListener("toast", handleToast)
    return () => window.removeEventListener("toast", handleToast)
  }, [])

  return { toasts, dismiss: (id: string) => setToasts((prev) => prev.filter((toast) => toast.id !== id)) }
}
