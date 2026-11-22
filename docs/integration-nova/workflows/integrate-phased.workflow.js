export const meta = {
  name: 'nova-integrate-phase',
  description: 'One NOVA integration: implement on its branch, two independent reviews, fix and re-verify',
  phases: [
    { title: 'Implement', detail: 'full-stack integrator on its worktree' },
    { title: 'Review', detail: 'fidelity/responsive/a11y + rules/correctness' },
    { title: 'Fix', detail: 'apply confirmed findings, re-run checks' },
  ],
}

const RULES = 'docs/integration-nova/common-rules.md'
const A = args

const RESULT = {
  type: 'object',
  properties: {
    status: { type: 'string', enum: ['done', 'partial', 'blocked'] },
    summary: { type: 'string' },
    headSha: { type: 'string' },
    commits: { type: 'array', items: { type: 'object', properties: { sha: { type: 'string' }, subject: { type: 'string' } }, required: ['sha', 'subject'] } },
    checks: { type: 'array', items: { type: 'object', properties: { name: { type: 'string' }, command: { type: 'string' }, result: { type: 'string', enum: ['pass', 'fail', 'not-run'] }, detail: { type: 'string' } }, required: ['name', 'result'] } },
    visual: { type: 'array', items: { type: 'object', properties: { screen: { type: 'string' }, width: { type: 'number' }, result: { type: 'string' }, screenshot: { type: 'string' } }, required: ['screen', 'result'] } },
    endpoints: { type: 'array', items: { type: 'string' } },
    migrations: { type: 'array', items: { type: 'object', properties: { file: { type: 'string' }, description: { type: 'string' } }, required: ['file', 'description'] } },
    publicApi: { type: 'array', items: { type: 'object', properties: { name: { type: 'string' }, path: { type: 'string' }, signature: { type: 'string' } }, required: ['name', 'path'] } },
    sharedNeeds: { type: 'array', items: { type: 'object', properties: { what: { type: 'string' }, why: { type: 'string' }, proposedOwner: { type: 'string' } }, required: ['what', 'why'] } },
    deviations: { type: 'array', items: { type: 'string' } },
    openIssues: { type: 'array', items: { type: 'string' } },
  },
  required: ['status', 'summary', 'headSha', 'commits', 'checks', 'openIssues'],
}

const FINDINGS = {
  type: 'object',
  properties: {
    verdict: { type: 'string' },
    findings: {
      type: 'array',
      items: {
        type: 'object',
        properties: {
          severity: { type: 'string', enum: ['blocker', 'major', 'minor'] },
          file: { type: 'string' },
          issue: { type: 'string' },
          evidence: { type: 'string' },
          suggestedFix: { type: 'string' },
        },
        required: ['severity', 'issue', 'suggestedFix'],
      },
    },
  },
  required: ['verdict', 'findings'],
}

const HEADER = `You are the ${A.role} for integration ${A.id} (${A.title}) of the NOVA Figma screens into the ERP monorepo.
Read FIRST and follow strictly: ${RULES} (common rules) and your brief ${A.brief}.
Worktree: ${A.worktree} — branch: ${A.branch} — scratch dir: ${A.scratch} (create it).
${A.phaseNote ? `THIS RUN: ${A.phaseNote}` : ''}`

phase('Implement')
const impl = await agent(`${HEADER}

Implement the whole brief end to end (frontend, API, database as the brief says). Work incrementally with small coherent commits on your branch following the commit rules. Before finishing, run every check listed in the common rules for the parts you touched (front: lint, tsc -b, test, build; back: mvn test through Docker for each touched service; python: pytest) and the visual verification at 375/768/1440 when you touched UI. Fix what fails. If something cannot be done, say exactly why in openIssues and set status partial. Return the structured result: headSha must be the output of git rev-parse HEAD in the worktree.`, { label: `${A.id}:implement`, phase: 'Implement', schema: RESULT })

if (!impl) return { id: A.id, error: 'implementer returned nothing' }

phase('Review')
const reviewBase = `${HEADER}

You are an independent REVIEWER. Do not modify, commit or stage anything in the worktree (read-only; you may write screenshots and scripts in ${A.scratch}/review). Review the diff of the branch against its base: git -C ${A.worktree} diff ${A.base}...HEAD (and git log ${A.base}..HEAD --format=fuller for commits). The implementer reported: ${JSON.stringify({ summary: impl.summary, status: impl.status, openIssues: impl.openIssues, deviations: impl.deviations || [] }).slice(0, 4000)}
Report only real, verified problems with evidence (file:line, command output, screenshot path). Severity: blocker (broken behaviour, rule violation explicitly forbidden: commits on protected branches, co-authored trailer, secrets, comments in code, file > 100 lines, API call in a component, error shown outside the error page, editable validated data, missing server-side permission), major (visible mismatch with Figma, broken responsive/a11y, missing test for new logic, wrong business rule, contract mismatch with the registry), minor (polish).`

const reviews = await parallel([
  () => agent(A.backendOnly ? `${reviewBase}

YOUR LENS: API contract and data behaviour (backend-only phase). Compare every endpoint, DTO field, status code, error type and table/column/constraint with the registry (docs/integration-nova/registry.md and registry-part-modules-eclipse.md) and the brief. Start the touched service(s) against a throwaway postgres container (unique container name with your id, free host port; remove it at the end), apply the integrator's migration scripts twice (idempotence) on a schema that already holds rows created by the PREVIOUS version of the service (checkout ${A.base} into a temporary git worktree under ${A.scratch}/review to build the old jar, insert data, then start the new version), and exercise the endpoints with curl sending X-User-Id / X-User-Email / X-User-Role headers. Report contract mismatches, migration problems on existing data, wrong computations, missing permission checks.` : `${reviewBase}

YOUR LENS: product fidelity, responsive and accessibility. Run the app UI as described in the common rules (Vite + Playwright with mocked /api/** and a fake session, or the real stack if the brief says so) and capture 375, 768, 1440 px and the breakpoint transitions. Compare with the Figma captures named in the brief. Check every interaction of the brief (filters, search, actions, menus, dialogs, keyboard navigation, focus visible and restored, labels, touch targets, long labels, empty/loading/error/success states, error flow to the error page). Check data formatting (fr-FR, Ar).`, { label: `${A.id}:review-ux`, phase: 'Review', schema: FINDINGS }),
  () => agent(`${reviewBase}

YOUR LENS: repository rules, correctness and tests. Check every changed file against the common rules (100 lines, no comments, layering, DTO-only, Reader/Creator/Updater/Deleter + interfaces, validation, typed errors, server-side permissions, immutability of validated data, migrations idempotent and compatible with existing data, contract conformity with the registry if it exists). Look for logic bugs (calculations, date/timezone, filters/counts, pagination, null handling, race conditions, double submit). Run the test suites yourself (front: npm test, lint, tsc -b; back: mvn test via Docker one service at a time) and report failures. Check commits: author/committer identity, message format, absence of any co-authored line (git log ${A.base}..HEAD --format=%B | grep -ic co-authored must be 0), no unrelated files committed.`, { label: `${A.id}:review-rules`, phase: 'Review', schema: FINDINGS }),
])

const findings = reviews.filter(Boolean).flatMap(r => r.findings || [])
const toFix = findings.filter(f => f.severity !== 'minor')
log(`${A.id}: ${findings.length} findings (${toFix.length} blocker/major)`)

phase('Fix')
const fixed = await agent(`${HEADER}

You are the integrator again, in a FIX pass. Independent reviewers reported the findings below. For each blocker/major finding: verify it, fix it if real (new commits, never amend), or explain why it is not real. Apply minor findings when cheap and safe. Then re-run all checks of the common rules for what you touched (and the visual verification if UI changed) and return the structured result for the final state of the branch (commits = all commits of the branch since ${A.base}; openIssues must list anything still not done, with the reason; deviations must list every finding you rejected and why).
Findings: ${JSON.stringify(findings).slice(0, 24000)}`, { label: `${A.id}:fix`, phase: 'Fix', schema: RESULT })

return { id: A.id, branch: A.branch, implement: impl, reviews: reviews.filter(Boolean), final: fixed }
