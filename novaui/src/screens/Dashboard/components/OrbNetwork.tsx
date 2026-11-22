import { buildOrbNetwork } from '../../../utils/orbNetwork'
import styles from './OrbNetwork.module.css'

const NETWORK = buildOrbNetwork(46, 21)

export const OrbNetwork = () => (
  <svg className={styles.network} viewBox="0 0 100 100" aria-hidden="true">
    {NETWORK.links.map((link, index) => (
      <line key={`l${index}`} {...link} className={styles.link} />
    ))}
    {NETWORK.nodes.map((node, index) => (
      <circle key={`n${index}`} cx={node.x} cy={node.y} r={node.r} className={styles.node} />
    ))}
  </svg>
)
