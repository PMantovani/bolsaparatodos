export class Balance {
  balance: number;
  date: Date;

  constructor(balance: number, date: string) {
    this.balance = balance;
    this.date = new Date(date);
  }
}
