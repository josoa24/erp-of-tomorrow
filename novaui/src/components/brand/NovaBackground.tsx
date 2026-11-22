import signatureLinesUrl from '../../assets/signature-lines.svg'

export const NovaBackground = () => (
  <div aria-hidden="true" className="pointer-events-none fixed inset-0 -z-10 overflow-hidden bg-canvas">
    <img
      src={signatureLinesUrl}
      alt=""
      width={1495.58}
      height={1139.61}
      className="absolute -top-[193px] -right-[662px] max-w-none"
      draggable={false}
    />
  </div>
)
