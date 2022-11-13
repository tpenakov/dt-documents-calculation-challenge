import {
  CreateParams,
  CreateResult,
  DataProvider,
  DeleteManyResult,
  DeleteResult,
  GetListParams,
  GetListResult,
  GetManyReferenceResult,
  GetManyResult,
  GetOneParams,
  GetOneResult,
  UpdateManyResult,
  UpdateParams,
  UpdateResult,
} from 'ra-core'
import { DefaultApi, CalculateResponse}  from '../../api-client/fetch'


export interface Operations {
  queries: Record<string, string>
  mutations: Record<string, string>
}


//reused from here: https://github.com/MrHertal/react-admin-amplify/blob/master/src/providers/DataProvider.ts
//documentation from here: https://marmelab.com/react-admin/DataProviderWriting.html
export class CustomDataProvider implements DataProvider {

  public getList = async (
    resource: string,
    params: GetListParams
  ): Promise<GetListResult> => {
    console.log('getList: ', params)
    
    return {
      data: [],
      total: 0,
    }
  }

  public getOne = async (
    resource: string,
    params: GetOneParams
  ): Promise<GetOneResult> => {
    console.log('getone: ', params)
   
    return {
      data: { id: 'any' },
    }
  }

  public getMany = async (resource: string): Promise<GetManyResult> => {
    return {
      data: [],
    }
  }

  public getManyReference = async (): Promise<GetManyReferenceResult> => {
    return {
      data: [],
      total: 0,
    }
  }

  public create = async (
    resource: string,
    params: CreateParams
  ): Promise<CreateResult> => {

    console.log('create: ', params)

    let exchangeRates = params.data.exchangeRates.map((rate: { currency: any; rate: any }) => `${rate.currency}:${rate.rate}`)

    console.log('exchangeRates: ', exchangeRates)

    let response: CalculateResponse = await new DefaultApi().sumInvoices( {
      file: params.data.file.rawFile, 
      exchangeRates: exchangeRates, 
      outputCurrency:  params.data.outputCurrency,
      customerVat: params.data.customerVat ? params.data.customerVat : ''})
    
    console.log('response: ', response)

    return {
      data: { id: 'any', ...response },
    }
  }

  public update = async (
    resource: string,
    params: UpdateParams
  ): Promise<UpdateResult> => {
    console.log('update: ', params)
   
    return {
      data: { id: 'any' },
    }
  }

  public updateMany = async (): Promise<UpdateManyResult> => {
    return {
      data: [],
    }
  }

  public delete = async (): Promise<DeleteResult> => {
    return {
      data: { id: 'any' },
    }
  }

  public deleteMany = async (): Promise<DeleteManyResult> => {
    return {
      data: [],
    }
  }
}
