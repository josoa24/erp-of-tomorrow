import { ModulePage } from '../../../components/module/ModulePage'
import { AccessCard } from '../components/AccessCard'
import { IdentityCard } from '../components/IdentityCard'
import { SecurityCard } from '../components/SecurityCard'
import { useProfile } from '../hooks/useProfile'

export const ProfileScreen = () => {
  const profile = useProfile()
  if (!profile) return null

  return (
    <ModulePage title="Mon profil">
      <div className="mx-auto flex w-full max-w-[720px] flex-col gap-4">
        <IdentityCard name={profile.name} email={profile.email} roleLabel={profile.roleLabel} />
        <AccessCard sentence={profile.sentence} accesses={profile.accesses} />
        <SecurityCard onLogout={profile.logout} />
      </div>
    </ModulePage>
  )
}
