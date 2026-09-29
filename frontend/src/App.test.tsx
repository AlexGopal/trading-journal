import { act, render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { afterEach, vi } from 'vitest'
import App from './App'
import { createTrade } from './api'

afterEach(() => vi.restoreAllMocks())

test('renders every approved field and validates required input', async () => {
  Object.defineProperty(window, 'innerWidth', { configurable: true, value: 375 })
  window.dispatchEvent(new Event('resize'))
  render(<App/>); const user = userEvent.setup()
  expect(screen.getByRole('heading', { name: /record a completed trade/i })).toBeVisible()
  for (const name of ['Ticker', 'Trade type', 'Entry date', 'Entry price', 'Exit date', 'Exit price', 'Number of shares']) expect(screen.getByLabelText(new RegExp(name, 'i'))).toBeVisible()
  await user.click(screen.getByLabelText(/trade type/i))
  await user.click(screen.getByRole('option', { name: 'Short' }))
  expect(screen.getByLabelText(/trade type/i)).toHaveTextContent('Short')
  await user.click(screen.getByRole('button', { name: /record trade/i }))
  expect(await screen.findByText('Ticker is required')).toBeVisible()
  screen.getByLabelText(/ticker/i).focus()
  expect(screen.getByLabelText(/ticker/i)).toHaveFocus()
})

test('shows backend field validation without false success', async () => {
  vi.stubGlobal('fetch', vi.fn().mockResolvedValue({ ok: false, json: async () => ({ message: 'Validation failed', fieldErrors: { ticker: 'Ticker is invalid' } }) }))
  render(<App/>); const user = userEvent.setup()
  for (const [label, value] of [['Ticker','AAPL'],['Entry date','2026-09-01'],['Entry price','220'],['Exit date','2026-09-15'],['Exit price','230'],['Number of shares','10']]) await user.type(screen.getByLabelText(new RegExp(label, 'i')), value)
  await user.click(screen.getByRole('button', { name: /record trade/i }))
  expect(await screen.findByText('Validation failed')).toBeVisible()
  expect(screen.getByText('Ticker is invalid')).toBeVisible()
  expect(screen.queryByText('Trade recorded')).not.toBeInTheDocument()
})

test('shows a stable loading state while the request is pending', async () => {
  let resolveRequest!: (value: object) => void
  vi.stubGlobal('fetch', vi.fn(() => new Promise(resolve => { resolveRequest = resolve })))
  render(<App/>); const user = userEvent.setup()
  for (const [label, value] of [['Ticker','AAPL'],['Entry date','2026-09-01'],['Entry price','220'],['Exit date','2026-09-15'],['Exit price','230'],['Number of shares','10']]) await user.type(screen.getByLabelText(new RegExp(label, 'i')), value)
  await user.click(screen.getByRole('button', { name: /record trade/i }))
  expect(await screen.findByRole('button', { name: /recording/i })).toBeDisabled()
  await act(async () => resolveRequest({ ok: true, json: async () => ({ id: 2, ticker: 'AAPL', tradeType: 'LONG', entryDate: '2026-09-01', entryPrice: 220, exitDate: '2026-09-15', exitPrice: 230, numberOfShares: 10, dollarPnl: 100, percentageReturn: 4.5455 }) }))
  expect(await screen.findByText('Trade recorded')).toBeVisible()
})

test('preserves accepted decimal text when serializing JSON numbers', async () => {
  vi.stubGlobal('fetch', vi.fn().mockResolvedValue({ ok: true, json: async () => ({}) }))
  await createTrade({ ticker: 'AAPL', tradeType: 'LONG', entryDate: '2026-09-01', entryPrice: '999999999999999.9999', exitDate: '2026-09-02', exitPrice: '0.0001', numberOfShares: '0.000001' })
  const body = vi.mocked(fetch).mock.calls[0][1]?.body as string
  expect(body).toContain('"entryPrice":999999999999999.9999')
  expect(body).toContain('"exitPrice":0.0001')
  expect(body).toContain('"numberOfShares":0.000001')
})

test('submits the contract and formats backend results to two decimals', async () => {
  vi.stubGlobal('fetch', vi.fn().mockResolvedValue({ ok: true, json: async () => ({ id: 1, ticker: 'AAPL', tradeType: 'LONG', entryDate: '2026-09-01', entryPrice: 220, exitDate: '2026-09-15', exitPrice: 230, numberOfShares: 10, dollarPnl: 100, percentageReturn: 4.5455 }) }))
  render(<App/>); const user = userEvent.setup()
  await user.type(screen.getByLabelText(/ticker/i), '  aapl  ')
  await user.type(screen.getByLabelText(/entry date/i), '2026-09-01')
  await user.type(screen.getByLabelText(/entry price/i), '220')
  await user.type(screen.getByLabelText(/exit date/i), '2026-09-15')
  await user.type(screen.getByLabelText(/exit price/i), '230')
  await user.type(screen.getByLabelText(/number of shares/i), '10')
  await user.click(screen.getByRole('button', { name: /record trade/i }))
  expect(await screen.findByText('100.00')).toBeVisible()
  expect(screen.getByText('4.55%')).toBeVisible()
  expect(fetch).toHaveBeenCalledWith('/api/v1/trades', expect.objectContaining({ method: 'POST' }))
  const request = JSON.parse(vi.mocked(fetch).mock.calls[0][1]?.body as string)
  expect(request).toEqual({ ticker: '  aapl  ', tradeType: 'LONG', entryDate: '2026-09-01', entryPrice: 220, exitDate: '2026-09-15', exitPrice: 230, numberOfShares: 10 })
})

test('shows technical failure without false success', async () => {
  vi.stubGlobal('fetch', vi.fn().mockResolvedValue({ ok: false, json: async () => ({ message: 'Unable to record trade' }) }))
  render(<App/>); const user = userEvent.setup()
  for (const [label, value] of [['Ticker','AAPL'],['Entry date','2026-09-01'],['Entry price','220'],['Exit date','2026-09-15'],['Exit price','230'],['Number of shares','10']]) await user.type(screen.getByLabelText(new RegExp(label, 'i')), value)
  await user.click(screen.getByRole('button', { name: /record trade/i }))
  expect(await screen.findByText('Unable to record trade')).toBeVisible()
  expect(screen.queryByText('Trade recorded')).not.toBeInTheDocument()
})

test('rejects date order and excess decimal scales before submission', async () => {
  const fetchMock = vi.fn()
  vi.stubGlobal('fetch', fetchMock)
  render(<App/>); const user = userEvent.setup()
  await user.type(screen.getByLabelText(/ticker/i), 'AAPL')
  await user.type(screen.getByLabelText(/entry date/i), '2026-09-15')
  await user.type(screen.getByLabelText(/entry price/i), '220.00001')
  await user.type(screen.getByLabelText(/exit date/i), '2026-09-01')
  await user.type(screen.getByLabelText(/exit price/i), '230')
  await user.type(screen.getByLabelText(/number of shares/i), '10.0000001')
  await user.click(screen.getByRole('button', { name: /record trade/i }))
  expect(await screen.findByText('Exit date must not precede entry date')).toBeVisible()
  expect(screen.getByText('Use no more than 4 fractional decimal places')).toBeVisible()
  expect(screen.getByText('Use no more than 6 fractional decimal places')).toBeVisible()
  expect(fetchMock).not.toHaveBeenCalled()
})
