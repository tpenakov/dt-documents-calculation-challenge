import { Datagrid, List, ListProps, TextField } from 'react-admin'

export const CustomerBalanceList = (props: ListProps) => (
  <List {...props}>
    <Datagrid isRowSelectable={() => false} rowClick={'edit'}>
      <TextField source="netId" sortable={false} />
      <TextField source="topic" />
      <TextField source="registry" />
      <TextField source="batches" />
      <TextField source="platformOperatorPrivateKey" />
      <TextField source="mnemonicPhrase" />
      <TextField source="rpcNode" />
      <TextField source="rpcNodeFallback" />
      <TextField source="fileClientApiKey" />
      <TextField source="fileClientApiUrl" />
      <TextField source="fileClientApiUploadUrl" />
      <TextField source="fileClientApiDownloadUrl" />
      <TextField source="fileClientCollectionDefault" />
    </Datagrid>
  </List>
)
