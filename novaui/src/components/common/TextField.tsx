import type { InputHTMLAttributes } from 'react'
import { cn } from '@/lib/utils'

interface TextFieldProps extends InputHTMLAttributes<HTMLInputElement> {
  label: string
}

export const TextField = ({ label, className, ...props }: TextFieldProps) => (
  <input
    aria-label={label}
    placeholder={label}
    className={cn(
      'h-11 w-full rounded-full border border-[rgba(140,142,255,0.5)] bg-[rgba(10,12,50,0.35)] px-5 text-[17px] transition duration-200 placeholder:text-nova-muted focus:border-[#a89bff] focus:shadow-[0_0_0_3px_rgba(123,92,255,0.25)] focus:outline-none',
      className,
    )}
    {...props}
  />
)
