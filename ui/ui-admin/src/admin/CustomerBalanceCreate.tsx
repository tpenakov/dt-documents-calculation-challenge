import { Create, required, SimpleForm, TextInput,FileInput,FileField, ArrayInput, SimpleFormIterator, NumberInput } from 'react-admin'

export const CustomerBalanceCreate = () => (
  <Create>
    <SimpleForm>
      <FileInput source="file" label="Upload CSV data" validate={[required()]}>
        <FileField source="src" title="title" />
      </FileInput>
      <ArrayInput  source="exchangeRates" validate={[required()]}>
        <SimpleFormIterator inline>
          <TextInput source="currency" helperText={false} />
          <NumberInput source="rate" helperText={false} />
        </SimpleFormIterator>
      </ArrayInput>
      <TextInput source="outputCurrency" validate={[required()]} />
      <TextInput source="customerVat" />
    </SimpleForm>
  </Create>
)
