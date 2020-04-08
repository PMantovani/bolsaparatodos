import { BrlCurrencyPipe } from './brl-currency.pipe';

describe('BrlCurrencyPipe', () => {
  it('create an instance', () => {
    const pipe = new BrlCurrencyPipe(null);
    expect(pipe).toBeTruthy();
  });
});
