import { useState, type RefObject } from 'react'
import { EclipseBar } from '../../../components/eclipse/EclipseBar'
import { ECLIPSE_UNAVAILABLE_LABEL } from '../../../constants/eclipsePlaceholders'
import { useEclipseAssistant } from '../hooks/useEclipseAssistant'
import { EclipsePanel, type PanelPlacement } from './EclipsePanel'

interface EclipseAssistantProps {
  variant: 'night' | 'light'
  placeholder: string
  moduleKey: string
  placement: PanelPlacement
  inputRef?: RefObject<HTMLTextAreaElement | null>
}

export const EclipseAssistant = ({ variant, placeholder, moduleKey, placement, inputRef }: EclipseAssistantProps) => {
  const [value, setValue] = useState('')
  const { chat, submit, status, unavailability, notice, visible, close } = useEclipseAssistant(moduleKey)

  return (
    <div className="relative">
      {visible && (
        <EclipsePanel
          chat={chat}
          notice={notice}
          unavailability={unavailability}
          placement={placement}
          onConfirm={chat.confirm}
          onCancel={chat.cancel}
          onRetry={(summary) => void submit(`Propose à nouveau : ${summary}`)}
          onReset={chat.reset}
          onClose={close}
        />
      )}
      <EclipseBar
        variant={variant}
        placeholder={placeholder}
        value={value}
        onChange={setValue}
        onSubmit={(text) => {
          setValue('')
          void submit(text)
        }}
        onStop={chat.stop}
        status={status}
        unavailableLabel={ECLIPSE_UNAVAILABLE_LABEL}
        inputRef={inputRef}
      />
    </div>
  )
}
