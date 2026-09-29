import { FormEvent, useState } from 'react'
import { Alert, Box, Button, Card, CardContent, CircularProgress, Container, MenuItem, Stack, TextField, Typography } from '@mui/material'
import { ApiError, createTrade, TradeResponse, TradeType } from './api'

type FormState = { ticker: string; tradeType: TradeType; entryDate: string; entryPrice: string; exitDate: string; exitPrice: string; numberOfShares: string }
const initial: FormState = { ticker: '', tradeType: 'LONG', entryDate: '', entryPrice: '', exitDate: '', exitPrice: '', numberOfShares: '' }

function validate(form: FormState) {
  const errors: Record<string, string> = {}
  if (!form.ticker.trim()) errors.ticker = 'Ticker is required'
  if (!form.entryDate) errors.entryDate = 'Entry date is required'
  if (!form.exitDate) errors.exitDate = 'Exit date is required'
  if (form.entryDate && form.exitDate && form.exitDate < form.entryDate) errors.exitDate = 'Exit date must not precede entry date'
  for (const [field, value, scale] of [['entryPrice', form.entryPrice, 4], ['exitPrice', form.exitPrice, 4], ['numberOfShares', form.numberOfShares, 6]] as const) {
    if (!value || Number(value) <= 0) errors[field] = 'Value must be greater than zero'
    else if ((value.split('.')[1]?.length ?? 0) > scale) errors[field] = `Use no more than ${scale} fractional decimal places`
  }
  return errors
}

export default function App() {
  const [form, setForm] = useState(initial)
  const [errors, setErrors] = useState<Record<string, string>>({})
  const [result, setResult] = useState<TradeResponse | null>(null)
  const [message, setMessage] = useState('')
  const [loading, setLoading] = useState(false)
  const change = (field: keyof FormState) => (event: React.ChangeEvent<HTMLInputElement>) => setForm({ ...form, [field]: event.target.value })

  async function submit(event: FormEvent) {
    event.preventDefault(); setResult(null); setMessage('')
    const nextErrors = validate(form); setErrors(nextErrors)
    if (Object.keys(nextErrors).length) return
    setLoading(true)
    try {
      const created = await createTrade(form)
      setResult(created)
    } catch (error) {
      if (error instanceof ApiError) { setMessage(error.message); setErrors(error.fieldErrors) }
      else setMessage('Unable to record trade')
    } finally { setLoading(false) }
  }

  return <Container maxWidth="md" sx={{ py: { xs: 3, md: 7 } }}>
    <Stack spacing={3}>
      <Box><Typography component="h1" variant="h3" fontWeight={700}>Record a completed trade</Typography><Typography color="text.secondary" mt={1}>Capture the completed stock trade and review its performance.</Typography></Box>
      <Card component="section" elevation={2} sx={{ minWidth: 0, maxWidth: '100%' }}><CardContent sx={{ minWidth: 0, p: { xs: 2.5, md: 4 } }}>
        <Box component="form" onSubmit={submit} noValidate sx={{ minWidth: 0, '& .MuiFormControl-root': { minWidth: 0, width: '100%' }, '& .MuiInputBase-root': { minWidth: 0 } }}><Stack spacing={2.5} sx={{ minWidth: 0 }}>
          <TextField label="Ticker" required value={form.ticker} onChange={change('ticker')} error={!!errors.ticker} helperText={errors.ticker}/>
          <TextField select label="Trade type" required value={form.tradeType} onChange={change('tradeType')}><MenuItem value="LONG">Long</MenuItem><MenuItem value="SHORT">Short</MenuItem></TextField>
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ minWidth: 0 }}>
            <TextField fullWidth label="Entry date" type="date" required slotProps={{ inputLabel: { shrink: true } }} value={form.entryDate} onChange={change('entryDate')} error={!!errors.entryDate} helperText={errors.entryDate}/>
            <TextField fullWidth label="Entry price" type="number" required value={form.entryPrice} onChange={change('entryPrice')} error={!!errors.entryPrice} helperText={errors.entryPrice} slotProps={{ htmlInput: { step: '0.0001', min: '0' } }}/>
          </Stack>
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ minWidth: 0 }}>
            <TextField fullWidth label="Exit date" type="date" required slotProps={{ inputLabel: { shrink: true } }} value={form.exitDate} onChange={change('exitDate')} error={!!errors.exitDate} helperText={errors.exitDate}/>
            <TextField fullWidth label="Exit price" type="number" required value={form.exitPrice} onChange={change('exitPrice')} error={!!errors.exitPrice} helperText={errors.exitPrice} slotProps={{ htmlInput: { step: '0.0001', min: '0' } }}/>
          </Stack>
          <TextField label="Number of shares" type="number" required value={form.numberOfShares} onChange={change('numberOfShares')} error={!!errors.numberOfShares} helperText={errors.numberOfShares} slotProps={{ htmlInput: { step: '0.000001', min: '0' } }}/>
          {message && <Alert severity="error">{message}</Alert>}
          <Button type="submit" variant="contained" size="large" disabled={loading}>{loading ? <><CircularProgress size={20} color="inherit" sx={{ mr: 1 }}/>Recording…</> : 'Record trade'}</Button>
        </Stack></Box>
      </CardContent></Card>
      {result && <Card component="section" aria-live="polite"><CardContent><Typography component="h2" variant="h5" fontWeight={700}>Trade recorded</Typography><Typography mt={1}>{result.ticker} · {result.tradeType} · Trade #{result.id}</Typography><Stack direction={{ xs: 'column', sm: 'row' }} spacing={4} mt={2}><Box><Typography color="text.secondary">Dollar P&amp;L</Typography><Typography variant="h4">{result.dollarPnl.toFixed(2)}</Typography></Box><Box><Typography color="text.secondary">Percentage return</Typography><Typography variant="h4">{result.percentageReturn.toFixed(2)}%</Typography></Box></Stack></CardContent></Card>}
    </Stack>
  </Container>
}

