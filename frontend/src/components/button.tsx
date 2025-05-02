// src/components/ui/button.tsx
import { ButtonHTMLAttributes } from "react"

export function Button(props: ButtonHTMLAttributes<HTMLButtonElement>) {
  return (
    <button
      {...props}
      className={`bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded ${props.className || ""}`}
    />
  )
}
