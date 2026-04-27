import { useAiSettingsStore } from '../stores/aiSettings'

export interface ChatMessage {
  role: 'system' | 'user' | 'assistant'
  content: string
}

export interface UploadedDoc {
  id: string
  name: string
  size: number
  uploadTime: string
}

const getConfig = () => {
  const store = useAiSettingsStore()
  return store.config
}

export const chatStream = async (
  messages: ChatMessage[],
  onDelta: (text: string) => void,
  onComplete?: (fullText: string) => void,
  onError?: (err: unknown) => void
): Promise<void> => {
  const config = getConfig()

  if (!config.baseUrl || !config.apiKey) {
    throw new Error('请先配置 AI 模型参数')
  }

  const url = config.baseUrl.replace(/\/$/, '') + '/chat/completions'

  const body = JSON.stringify({
    model: config.model,
    messages,
    temperature: config.temperature,
    stream: true
  })

  try {
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${config.apiKey}`
      },
      body
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
  const config = getConfig()
  const formData = new FormData()
  formData.append('file', file)

  const response = await fetch('/api/ai/document/upload', {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${config.apiKey}`
    },
    body: formData
  })

  if (!response.ok) {
    throw new Error(`文档上传失败 (${response.status})`)
  }

  return response.json()
}

export const getUploadedDocuments = async (): Promise<UploadedDoc[]> => {
  const response = await fetch('/api/ai/documents')

  if (!response.ok) {
    throw new Error(`获取文档列表失败 (${response.status})`)
  }

  return response.json()
}

export const deleteDocument = async (id: string): Promise<void> => {
  const response = await fetch(`/api/ai/document/${id}`, {
    method: 'DELETE'
  })

  if (!response.ok) {
    throw new Error(`删除文档失败 (${response.status})`)
  }
}
