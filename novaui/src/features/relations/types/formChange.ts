export type FieldChange<Values> = <Key extends keyof Values>(key: Key, value: Values[Key]) => void
