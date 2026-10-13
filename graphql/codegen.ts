import type { CodegenConfig } from '@graphql-codegen/cli'

const config: CodegenConfig = {
  overwrite: true,
  schema: ['./schema/**/*.graphql'],
  documents: [
    '../sudoprivacyinteraction/src/main/graphql/com/sudoplatform/sudoprivacyinteraction/documents/**/*.graphql',
  ],
  generates: {
    '../sudoprivacyinteraction/src/main/graphql/com/sudoplatform/sudoprivacyinteraction/schema.json': {
      plugins: ['introspection'],
    },
  },
  ignoreNoDocuments: true,
}

export default config
