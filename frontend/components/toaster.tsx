"use client"

import { useToast } from "./use-toast"
import { X } from "lucide-react"

export function Toaster() {
  const { toasts, dismiss } = useToast()

  return (
    <div className="fixed top-0 right-0 p-4 w-full md:max-w-sm z-50 flex flex-col gap-2">
      {toasts.map((toast) => (
        <div
          key={toast.id}
          className={`bg-white border shadow-lg rounded-lg overflow-hidden pointer-events-auto flex ${
            toast.variant === "destructive" ? "border-red-500" : "border-gray-200"
          }`}
        >
          <div className="flex-1 p-4">
            {toast.title && (
              <div className={`font-medium ${toast.variant === "destructive" ? "text-red-600" : ""}`}>
                {toast.title}
              </div>
            )}
            {toast.description && <div className="text-sm text-gray-500 mt-1">{toast.description}</div>}
          </div>
          <button
            onClick={() => dismiss(toast.id)}
            className="flex items-center justify-center w-10 h-10 border-l border-gray-200 hover:bg-gray-100"
          >
            <X className="h-4 w-4" />
          </button>
        </div>
      ))}
    </div>
  )
}
