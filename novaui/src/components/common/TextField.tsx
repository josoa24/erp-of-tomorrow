import type { InputHTMLAttributes } from 'react'
import styles from './TextField.module.css'

interface TextFieldProps extends InputHTMLAttributes<HTMLInputElement> {
  label: string
}

export const TextField = ({ label, className = '', ...props }: TextFieldProps) => (
  <input
    aria-label={label}
    placeholder={label}
    className={`${styles.input} ${className}`}
    {...props}
  />
)
