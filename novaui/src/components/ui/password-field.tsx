import { Eye, EyeOff } from 'lucide-react';
import { useState } from 'react';
import { IconButton } from './icon-button';
import { Input } from './input';
import type { InputProps } from './input';

export type PasswordFieldProps = Omit<InputProps, 'type' | 'trailing' | 'label'> & { label?: string };

export function PasswordField({ label = 'Mot de passe', autoComplete = 'current-password', ...props }: PasswordFieldProps) {
  const [visible, setVisible] = useState(false);

  return (
    <Input
      label={label}
      type={visible ? 'text' : 'password'}
      autoComplete={autoComplete}
      trailing={
        <IconButton
          size="s"
          aria-label="Afficher le mot de passe"
          aria-pressed={visible}
          onClick={() => setVisible((current) => !current)}
          className="[&_svg]:size-5"
        >
          {visible ? <EyeOff /> : <Eye />}
        </IconButton>
      }
      {...props}
    />
  );
}
