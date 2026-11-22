import type { ButtonHTMLAttributes } from 'react'
import styles from './PillButton.module.css'

interface PillButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'light' | 'ghost'
}

export const PillButton = ({ variant = 'light', className = '', ...props }: PillButtonProps) => (
  <button className={`${styles.button} ${styles[variant]} ${className}`} {...props} />
)
