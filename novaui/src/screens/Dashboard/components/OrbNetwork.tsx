import { buildOrbNetwork } from '../../../utils/orbNetwork'

const NETWORK = buildOrbNetwork(46, 21)

export const OrbNetwork = () => (
  <svg className="absolute inset-0 size-full rounded-full mask-[radial-gradient(circle,#000_45%,rgba(0,0,0,0.55)_72%,#000_100%)]" viewBox="0 0 100 100" aria-hidden="true">
    {NETWORK.links.map((link, index) => (
      <line key={`l${index}`} {...link} className="stroke-[rgba(150,170,255,0.22)] stroke-[0.18]" />
    ))}
    {NETWORK.nodes.map((node, index) => (
      <circle key={`n${index}`} cx={node.x} cy={node.y} r={node.r} className="animate-twinkle fill-[#b9c8ff] drop-shadow-[0_0_1px_rgba(170,190,255,0.95)] motion-reduce:animate-none [&:nth-child(3n)]:fill-[#c7a6ff] [&:nth-child(3n)]:[animation-delay:1.3s] [&:nth-child(4n)]:[animation-delay:2.4s]" />
    ))}
  </svg>
)
