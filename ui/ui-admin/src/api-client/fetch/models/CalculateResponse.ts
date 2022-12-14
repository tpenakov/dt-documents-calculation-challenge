/* tslint:disable */
/* eslint-disable */
/**
 * Invoicing API challenge
 * Design Technologies challenges you to create an API (PHP / Java application) that lets you sum invoice documents in different currencies via a file.  This is a small task to evaluate potential hires.  ## The task  We have a **CSV** file, containing a list of invoices, debit and credit notes in different currencies. **Document structure** with **demo data** can be found in the [`data.csv`](./data.csv).  API endpoint should allow you to pass: - CSV file - A list of currencies and exchange rates (for example: `EUR:1,USD:0.987,GBP:0.878`) - An output currency (for example: `GBP`) - Filter by a specific customer by VAT number (as an optional input)  Keep in mind that the exchange rates are always based on the default currency. The default currency is specified by giving it an exchange rate of 1. EUR is used as a default currency only for the example. For example: ``` EUR = 1 EUR:USD = 0.987 EUR:GBP = 0.878 ```  The response should contain **the sum of all documents per customer**. If the optional input filter is used, the functionality should **return only the sum of the invoices for that specific customer**.  Invoice types: - 1 = invoice - 2 = credit note - 3 = debit note  Note, that if we have a credit note, it should subtract from the total of the invoice and if we have a debit note, it should add to the sum of the invoice.   ## Requirements  - The application MUST use only in memory storage. - The application MUST comply to the PSR-2 coding standard and use a PSR-4 autoloader (for PHP applications). - The application MUST be covered by unit tests. - The application MUST support different currencies. - The application MUST validate the input (for example: show an error if an unsupported currency is passed; show an error if a document has a specified parent, but the parent is missing, etc.) - OOP best practices MUST be followed. - The application MUST be supplied in a public git repository. - Setup instructions MUST be provided. - Your application MUST be fully compatible with the provided [`openapi.yaml`](./openapi.yaml) definition. - Optional: the application should have a client side, implemented in any modern JavaScript framework (e.g. React.js, Angular.js, etc.) 
 *
 * The version of the OpenAPI document: 1.0.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import { exists, mapValues } from '../runtime';
import type { Customer } from './Customer';
import {
    CustomerFromJSON,
    CustomerFromJSONTyped,
    CustomerToJSON,
} from './Customer';

/**
 * 
 * @export
 * @interface CalculateResponse
 */
export interface CalculateResponse {
    /**
     * 
     * @type {string}
     * @memberof CalculateResponse
     */
    currency?: string;
    /**
     * 
     * @type {Array<Customer>}
     * @memberof CalculateResponse
     */
    customers?: Array<Customer>;
}

/**
 * Check if a given object implements the CalculateResponse interface.
 */
export function instanceOfCalculateResponse(value: object): boolean {
    let isInstance = true;

    return isInstance;
}

export function CalculateResponseFromJSON(json: any): CalculateResponse {
    return CalculateResponseFromJSONTyped(json, false);
}

export function CalculateResponseFromJSONTyped(json: any, ignoreDiscriminator: boolean): CalculateResponse {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'currency': !exists(json, 'currency') ? undefined : json['currency'],
        'customers': !exists(json, 'customers') ? undefined : ((json['customers'] as Array<any>).map(CustomerFromJSON)),
    };
}

export function CalculateResponseToJSON(value?: CalculateResponse | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'currency': value.currency,
        'customers': value.customers === undefined ? undefined : ((value.customers as Array<any>).map(CustomerToJSON)),
    };
}

