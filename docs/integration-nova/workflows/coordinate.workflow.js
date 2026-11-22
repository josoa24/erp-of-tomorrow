export const meta = {
  name: 'nova-coordinate',
  description: 'Coordination pass: check every integration branch against the shared registry and against each other',
  phases: [{ title: 'Coordinate', detail: 'registry conformity + cross-branch conflicts' }],
}

const B = '<SCRATCH>/briefs'

const OUT = {
  type: 'object',
  properties: {
    registryStatus: { type: 'array', items: { type: 'object', properties: { id: { type: 'string' }, state: { type: 'string' }, note: { type: 'string' } }, required: ['id', 'state'] } },
    mismatches: { type: 'array', items: { type: 'object', properties: { contract: { type: 'string' }, producerBranch: { type: 'string' }, consumerBranch: { type: 'string' }, problem: { type: 'string' }, decisionNeeded: { type: 'boolean' }, proposedResolution: { type: 'string' } }, required: ['contract', 'problem', 'proposedResolution'] } },
    overlappingFiles: { type: 'array', items: { type: 'object', properties: { file: { type: 'string' }, branches: { type: 'array', items: { type: 'string' } }, risk: { type: 'string' }, owner: { type: 'string' } }, required: ['file', 'branches', 'risk'] } },
    actions: { type: 'array', items: { type: 'object', properties: { branch: { type: 'string' }, action: { type: 'string' } }, required: ['branch', 'action'] } },
    registryMarkdown: { type: 'string' },
  },
  required: ['registryStatus', 'mismatches', 'overlappingFiles', 'actions', 'registryMarkdown'],
}

phase('Coordinate')
return await agent(`You are the INTER-AGENT COORDINATION agent of the NOVA Figma integration. READ-ONLY on the repository: never modify, commit, checkout or merge anything (you may write only under ${B}/../coord/).
Read: ${B}/registry.md, ${B}/registry-part-modules-eclipse.md, ${B}/common-rules.md and every brief ${B}/I*.md.
Repository: /home/saandria/Documents/devfest/erp-of-tomorrow (protected base branch songomby_d2). Integration branches (each in its own worktree under /home/saandria/Documents/devfest/erp-of-tomorrow.worktrees/): ${JSON.stringify(args.branches)}.
For each branch: git log songomby_d2..<branch> --oneline and git diff songomby_d2...<branch> (also look at uncommitted work in its worktree with git -C <worktree> status --short, only to know what is in progress).
Tasks:
1. For every registry element (R1..R11, C1..C3, S*, V*, P*, H*, A1): state (not started / in progress / implemented / implemented with deviation) with evidence.
2. Contract conformity between producers and consumers ACROSS branches: paths, HTTP methods, JSON field names and types, enums, status codes, role rules, table/column names, migration numbering ranges, module/adaptation keys, front hook signatures and component props published by the socle (I0) versus what consumers expect. Flag every mismatch, say whether it needs an orchestrator decision, and propose the resolution that keeps the registry as the source of truth (or, if the producer's version is objectively better, propose updating the registry and list consumers to notify).
3. Files changed by more than one branch (git diff --name-only intersections): risk of conflict, owner per the registry, recommended merge order.
4. Concrete actions per branch (what each integrator must change to align).
5. Return the full updated registry as markdown in registryMarkdown (same structure as registry.md, with the State column updated and a "Décisions et écarts" section listing each mismatch and its proposed resolution). Be factual and cite commits/files.`, { label: 'coordination', phase: 'Coordinate', schema: OUT })
