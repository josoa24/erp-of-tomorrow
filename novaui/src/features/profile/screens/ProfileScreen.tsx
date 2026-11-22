import { ModulePage } from '../../../components/module/ModulePage'
import { AccessCard } from '../components/AccessCard'
import { AdminCard } from '../components/AdminCard'
import { IdentityCard } from '../components/IdentityCard'
import { SecurityCard } from '../components/SecurityCard'
import { useProfile } from '../hooks/useProfile'

export const ProfileScreen = () => {
  const profile = useProfile()
  if (!profile) return null

  return (
    <ModulePage title="Mon profil" subtitle="Vos informations et vos accès dans NOVA.">
      <div className="mx-auto flex w-full max-w-[960px] flex-col gap-4">
        <div className="grid gap-4 lg:grid-cols-2">
          <IdentityCard name={profile.name} email={profile.email} roles={profile.roles} />
          <AccessCard sentence={profile.sentence} accesses={profile.accesses} />
        </div>
        {profile.isAdmin && <AdminCard onOpen={profile.openUsers} />}
        <SecurityCard onLogout={profile.logout} />
      </div>
    </ModulePage>
  )
}
