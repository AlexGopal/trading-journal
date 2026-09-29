export type TradeType = 'LONG' | 'SHORT'
export interface CreateTradeRequest { ticker: string; tradeType: TradeType; entryDate: string; entryPrice: string; exitDate: string; exitPrice: string; numberOfShares: string }
export interface TradeResponse { id: number; ticker: string; tradeType: TradeType; entryDate: string; entryPrice: number; exitDate: string; exitPrice: number; numberOfShares: number; dollarPnl: number; percentageReturn: number }
export interface ValidationErrorResponse { message: string; fieldErrors: Record<string, string> }

export class ApiError extends Error {
  constructor(message: string, public fieldErrors: Record<string, string> = {}) { super(message) }
}

function decimalLiteral(value: string): string {
  const match = /^(\d*)(?:\.(\d*))?([eE][+-]?\d+)?$/.exec(value.trim())
  if (!match || (!match[1] && !match[2])) throw new ApiError('Validation failed')
  const integer = (match[1] || '0').replace(/^0+(?=\d)/, '')
  const fraction = match[2] === undefined ? '' : `.${match[2]}`
  return `${integer}${fraction}${match[3] ?? ''}`
}

function requestBody(request: CreateTradeRequest): string {
  return `{${[
    `"ticker":${JSON.stringify(request.ticker)}`,
    `"tradeType":${JSON.stringify(request.tradeType)}`,
    `"entryDate":${JSON.stringify(request.entryDate)}`,
    `"entryPrice":${decimalLiteral(request.entryPrice)}`,
    `"exitDate":${JSON.stringify(request.exitDate)}`,
    `"exitPrice":${decimalLiteral(request.exitPrice)}`,
    `"numberOfShares":${decimalLiteral(request.numberOfShares)}`,
  ].join(',')}}`
}

export async function createTrade(request: CreateTradeRequest): Promise<TradeResponse> {
  const response = await fetch('/api/v1/trades', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: requestBody(request) })
  const body = await response.json().catch(() => ({ message: 'Unable to record trade' }))
  if (!response.ok) throw new ApiError(body.message ?? 'Unable to record trade', body.fieldErrors ?? {})
  return body as TradeResponse
}

