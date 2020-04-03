import { Component, OnInit, Inject } from '@angular/core';
import { ApiClientService } from 'src/app/services/api-client.service';
import { Chart, ChartTooltipItem, ChartOptions } from 'chart.js';
import { DatePipe, DOCUMENT } from '@angular/common';
import { BrlCurrencyPipe } from 'src/app/pipes/brl-currency.pipe';
import { Balance } from 'src/app/model/Balance';
import * as moment from 'moment';
import { HttpParams } from '@angular/common/http';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {

  public chartOptions: ChartOptions = {
    responsive: true,
    legend: {
      display: false
    },
    scales: {
      yAxes: [{
        id: 'y-axis',
        type: 'linear',
        position: 'left',
        ticks: {
          min: 0,
          fontSize: 14
        },
        gridLines: {
          borderDash: [5, 5],
          drawBorder: false
        },
      }],
      xAxes: [{
        id: 'x-axis',
        type: 'time',
        position: 'bottom',
        gridLines: {
          display: false
        },
        ticks: {
          autoSkipPadding: 10
        }
      }]
    },
    tooltips: {
      intersect: false, mode: 'index',
      backgroundColor: 'rgba(255, 255, 255, 0.8)',
      titleFontColor: '#000',
      bodyFontColor: '#000',
      callbacks: {
        title: (item) => this.formatTooltipTitle(item),
        label: (item) => this.formatTooltipLabel(item)
      }
    }
  };
  public chart: Chart;

  constructor(
    private api: ApiClientService,
    private datePipe: DatePipe,
    private currencyPipe: BrlCurrencyPipe,
    @Inject(DOCUMENT) private document: Document) {
  }

  ngOnInit() {
    moment.locale('pt-BR');
    const canvas = this.document.getElementById('balanceChart') as HTMLCanvasElement;
    const ctx = canvas.getContext('2d');

    this.chart = new Chart(ctx, {
      type: 'line',
      options: this.chartOptions,
      data: {
        labels: [],
        datasets: [{
          data: [],
          label: 'Saldo',
          pointRadius: 0,
          yAxisID: 'y-axis',
          xAxisID: 'x-axis',
          backgroundColor: 'rgba(139, 191, 138, 0.4)',
          borderColor: 'rgb(139,191,138)',
          pointBackgroundColor: 'rgb(139,191,138)',
          pointBorderColor: 'rgb(139,191,138)',
          cubicInterpolationMode: 'monotone'
        }]
      }
    });

    const queryParams = new HttpParams()
      .set('startDate', '2020-01-03T00:00:00')
      .set('endDat', '2020-03-24T18:00:00');

    this.api.get('balance', queryParams).subscribe((balances: Balance[]) => {
      this.chart.data.datasets[0].data = balances.map(i => i.balance);
      this.chart.data.labels = balances.map(i => i.date.toString());
      this.chart.update();
    });
  }

  formatTooltipTitle(item: ChartTooltipItem[]): string {
    if (item.length > 0 && item[0].label) {
      return this.datePipe.transform(item[0].label, 'mediumDate');
    }
  }

  formatTooltipLabel(item: ChartTooltipItem): string {
    return 'Patrimônio: ' + this.currencyPipe.transform(item.value);
  }
}
