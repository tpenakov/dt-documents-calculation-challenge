// in src/admin/index.tsx
import { Admin, Resource, DataProvider } from "react-admin";
import { CustomDataProvider } from './provider/CustomDataProvider'
import { CustomerBalanceCreate } from "./CustomerBalanceCreate";
import { CustomerBalanceList } from "./CustomerBalanceList";
import { CustomerBalanceUpdate } from "./CustomerBalanceUpdate";

// const dataProvider = jsonServerProvider("https://jsonplaceholder.typicode.com");
const dataProvider = new CustomDataProvider() as DataProvider

const App = () => (
  <Admin dataProvider={dataProvider}>
        <Resource
      name="customerBalance"
      list={CustomerBalanceList} 
      create={CustomerBalanceCreate}
      edit={CustomerBalanceUpdate}
      options={{ label: 'Customer(s) balance' }}
    />
  </Admin>
);

export default App;

