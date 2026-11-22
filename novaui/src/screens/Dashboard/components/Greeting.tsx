import { getGreeting } from '../../../utils/greeting'

export const Greeting = () => (
  <div className="mt-7.5">
    <h1 className="text-[52px] font-medium tracking-[-0.01em] max-sm:text-4xl">{getGreeting(new Date().getHours())}</h1>
    <p className="mt-2 text-[28px] font-light text-nova-soft max-sm:text-[19px]">Votre activité est vivante et en mouvement.</p>
  </div>
)
