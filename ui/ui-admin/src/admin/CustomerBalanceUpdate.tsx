import { SimpleForm, TextInput, ArrayInput, SimpleFormIterator, NumberInput, Edit } from 'react-admin'

export const CustomerBalanceUpdate = () => (
  <Edit>
    <SimpleForm>
      <TextInput source="currency" disabled />
      <ArrayInput  source="customers">
        <SimpleFormIterator inline>
          <TextInput source="name" disabled />
          <NumberInput source="balance" disabled />
        </SimpleFormIterator>
      </ArrayInput>
    </SimpleForm>
  </Edit>
)
