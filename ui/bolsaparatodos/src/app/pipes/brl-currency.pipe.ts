import { Pipe, PipeTransform } from '@angular/core';
import { CurrencyPipe } from '@angular/common';

@Pipe({
  name: 'brlCurrency'
})
export class BrlCurrencyPipe extends CurrencyPipe implements PipeTransform {

  transform(
    value: any,
    currencyCode?: string,
    display?: 'code' | 'symbol' | 'symbol-narrow' | string | boolean,
    digitsInfo?: string,
    locale?: string): string | null {

    return super.transform(
      value,
      currencyCode || 'BRL',
      display || 'symbol',
      digitsInfo,
      locale || 'pt'
    );
  }

}
