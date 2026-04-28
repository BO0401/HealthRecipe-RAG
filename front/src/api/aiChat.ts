import http from './http'
import { UploadedDocSchema, type ChatMessage, type UploadedDoc } from '../types/api'

export const chatStream = async (
  messages: ChatMessage[],
  onDelta: (text: string) => void,
  onComplete?: (fullText: string) => void,
  onError?: (err: unknown) => void
): Promise<void> => {
  try {
    const response = await fetch('/api/ai/chat', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ messages })
    })

    if (!response.ok || !response.body) {
      const errText = await response.text().catch(() => '')
      throw new Error(`请求失败 (${response.status}): ${errText || response.statusText}`)
    }

    const reader = response.body.getReader()
    const decoder = new TextDecoder('utf-8')
    let buffer = ''
    let fullText = ''

    while (true) {
      const { value, done } = await reader.read()
      if (done) break

      buffer += decoder.decode(value, { stream: true })
      const parts = buffer.split('\n\n')
      buffer = parts.pop() || ''

      for (const part of parts) {
        const lines = part.split('\n').map(l => l.trim()).filter(Boolean)
        for (const line of lines) {
          if (!line.startsWith('data:')) continue
          const data = line.slice(5).trim()
          if (data === '[DONE]') {
            if (onComplete) onComplete(fullText)
            return
          }
          try {
            const json = JSON.parse(data)
            const delta = json.choices?.[0]?.delta?.content ?? json.choices?.[0]?.message?.content ?? ''
            if (delta) {
              fullText += delta
              onDelta(delta)
            }
          } catch {
            // skip non-json lines
          }
        }
      }
    }

    if (onComplete) onComplete(fullText)
  } catch (err) {
    if (onError) onError(err)
    else console.error('[aiChat] stream error:', err)
    throw err
  }
}

export const uploadDocument = async (file: File): Promise<UploadedDoc> => {
  const formData = new FormData()
  formData.append('file', file)

  const response = await http.post('/ai/document/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })

  return UploadedDocSchema.parse(response)
}

export const getUploadedDocuments = async (): Promise<UploadedDoc[]> => {
  const response = await http.get<UploadedDoc[]>('/ai/documents')
  return response.map(item => UploadedDocSchema.parse(item))
}

export const deleteDocument = async (id: string): Promise<void> => {
  await http.delete(`/ai/document/${id}`)
}
