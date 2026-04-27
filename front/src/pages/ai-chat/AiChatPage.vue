<script setup lang="ts">
import { ref, nextTick, onMounted } from 'vue'
import { useAiSettingsStore } from '../../stores/aiSettings'
import { chatStream, uploadDocument, getUploadedDocuments, deleteDocument } from '../../api/aiChat'
import type { ChatMessage, UploadedDoc } from '../../api/aiChat'
import {
  Promotion,
  Delete,
  Upload,
  Document,
  Setting,
  Refresh,
  ChatLineSquare,
  User,
  Plus
} from '@element-plus/icons-vue'

const aiSettings = useAiSettingsStore()

const messages = ref<ChatMessage[]>([])
const inputText = ref('')
const loading = ref(false)
const chatContainer = ref<HTMLElement | null>(null)

const docs = ref<UploadedDoc[]>([])
const uploading = ref(false)

const settingsVisible = ref(false)
const settingsForm = ref({ ...aiSettings.config })

const scrollToBottom = async () => {
  await nextTick()
  if (chatContainer.value) {
    chatContainer.value.scrollTop = chatContainer.value.scrollHeight
  }
}

const sendMessage = async () => {
  const text = inputText.value.trim()
  if (!text || loading.value) return

  inputText.value = ''
  messages.value.push({ role: 'user', content: text })
  await scrollToBottom()

  loading.value = true
  messages.value.push({ role: 'assistant', content: '' })
  await scrollToBottom()

  const systemPrompt: ChatMessage = {
    role: 'system',
    content: '你是一位专业的营养健康助手，擅长回答关于饮食营养、健康食谱、食材搭配等方面的问题。请用中文回答，回答要专业、准确、易懂。'
  }

  const history = messages.value.slice(0, -1).map(m => ({
    role: m.role as 'user' | 'assistant',
    content: m.content
  }))

  try {
    await chatStream(
      [systemPrompt, ...history],
      (delta) => {
        const last = messages.value[messages.value.length - 1]
        if (last && last.role === 'assistant') {
          last.content += delta
        }
        scrollToBottom()
      },
      () => {
        loading.value = false
        scrollToBottom()
      },
      (err: any) => {
        loading.value = false
        const last = messages.value[messages.value.length - 1]
        if (last && last.role === 'assistant') {
          last.content = `请求失败：${err.message || '请检查模型配置后重试'}`
        }
        scrollToBottom()
      }
    )
  } catch (err: any) {
    loading.value = false
    const last = messages.value[messages.value.length - 1]
    if (last && last.role === 'assistant') {
      last.content = `请求失败：${err.message || '请检查模型配置后重试'}`
    }
    scrollToBottom()
  }
}

const handleKeydown = (e: KeyboardEvent) => {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    sendMessage()
  }
}

const clearChat = () => {
  messages.value = []
}

const fetchDocs = async () => {
  try {
    docs.value = await getUploadedDocuments()
  } catch {
    console.warn('[aiChat] failed to fetch documents')
  }
}

const handleUpload = async (file: File) => {
  uploading.value = true
  try {
    const doc = await uploadDocument(file)
    docs.value.unshift(doc)
  } catch (err: any) {
    console.error('[aiChat] upload error:', err)
  } finally {
    uploading.value = false
  }
}

const handleRemoveDoc = async (id: string) => {
  try {
    await deleteDocument(id)
    docs.value = docs.value.filter(d => d.id !== id)
  } catch (err: any) {
    console.error('[aiChat] delete doc error:', err)
  }
}

const formatSize = (bytes: number): string => {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}

const openSettings = () => {
  settingsForm.value = { ...aiSettings.config }
  settingsVisible.value = true
}

const saveSettings = () => {
  aiSettings.updateConfig(settingsForm.value)
  settingsVisible.value = false
}

onMounted(() => {
  fetchDocs()
})
</script>

<template>
  <div class="ai-chat-page">
    <div class="chat-layout">
      <div class="chat-main">
        <div class="chat-header">
          <div class="header-info">
            <h2 class="header-title">
              <el-icon :size="20"><ChatLineSquare /></el-icon>
              AI 营养助手
            </h2>
            <p class="header-desc">专业饮食营养建议，智能健康食谱推荐</p>
          </div>
          <div class="header-actions">
            <el-tooltip content="清空对话" placement="bottom">
              <el-button :icon="Delete" circle size="small" @click="clearChat" />
            </el-tooltip>
            <el-tooltip content="模型设置" placement="bottom">
              <el-button :icon="Setting" circle size="small" @click="openSettings" />
            </el-tooltip>
          </div>
        </div>

        <div ref="chatContainer" class="chat-messages">
          <div v-if="messages.length === 0" class="chat-welcome">
            <div class="welcome-icon">
              <el-icon :size="48"><ChatLineSquare /></el-icon>
            </div>
            <h3>你好！我是 AI 营养助手</h3>
            <p class="welcome-text">我可以帮你解答饮食营养问题、推荐健康食谱、分析食材搭配</p>
            <div class="welcome-suggestions">
              <el-button
                v-for="(s, i) in ['减脂期应该怎么吃？', '蛋白质含量高的食物有哪些？', '帮我设计一份一周健康食谱']"
                :key="i"
                size="small"
                round
                @click="inputText = s"
              >
                {{ s }}
              </el-button>
            </div>
          </div>

          <div
            v-for="(msg, idx) in messages"
            :key="idx"
            class="message-row"
            :class="msg.role === 'user' ? 'message-user' : 'message-ai'"
          >
            <div class="message-avatar">
              <el-icon v-if="msg.role === 'user'" :size="18"><User /></el-icon>
              <el-icon v-else :size="18" color="#409eff"><ChatLineSquare /></el-icon>
            </div>
            <div class="message-bubble" :class="msg.role === 'user' ? 'bubble-user' : 'bubble-ai'">
              <div class="message-content">{{ msg.content || (loading && idx === messages.length - 1 ? '思考中...' : '') }}</div>
            </div>
          </div>
        </div>

        <div class="chat-input-area">
          <el-input
            v-model="inputText"
            type="textarea"
            :rows="2"
            placeholder="输入你的营养健康问题..."
            :disabled="loading"
            @keydown="handleKeydown"
          />
          <div class="input-actions">
            <span class="input-hint">按 Enter 发送，Shift+Enter 换行</span>
            <el-button
              type="primary"
              :icon="Promotion"
              :loading="loading"
              :disabled="!inputText.trim()"
              @click="sendMessage"
            >
              发送
            </el-button>
          </div>
        </div>
      </div>

      <aside class="chat-sidebar">
        <div class="sidebar-section">
          <h3 class="sidebar-title">
            <el-icon :size="16"><Upload /></el-icon>
            文档上传
          </h3>
          <el-upload
            drag
            :auto-upload="false"
            :show-file-list="false"
            :on-change="(u: any) => u.raw && handleUpload(u.raw)"
            accept=".pdf,.doc,.docx,.txt,.md"
          >
            <el-icon :size="32" class="upload-icon"><Upload /></el-icon>
            <div class="upload-text">
              <span>拖拽或点击上传文档</span>
              <span class="upload-hint">支持 PDF、Word、TXT、Markdown 格式</span>
            </div>
          </el-upload>
        </div>

        <div class="sidebar-section">
          <h3 class="sidebar-title">
            <el-icon :size="16"><Document /></el-icon>
            已上传文档
            <span class="doc-count">({{ docs.length }})</span>
          </h3>
          <div v-if="docs.length === 0" class="sidebar-empty">
            <p>暂无上传文档</p>
          </div>
          <div v-else class="doc-list">
            <div v-for="doc in docs" :key="doc.id" class="doc-item">
              <div class="doc-info">
                <el-icon :size="16" color="#409eff"><Document /></el-icon>
                <div class="doc-meta">
                  <span class="doc-name">{{ doc.name }}</span>
                  <span class="doc-size">{{ formatSize(doc.size) }}</span>
                </div>
              </div>
              <el-button
                type="danger"
                size="small"
                :icon="Delete"
                circle
                @click="handleRemoveDoc(doc.id)"
              />
            </div>
          </div>
        </div>
      </aside>
    </div>

    <el-dialog
      v-model="settingsVisible"
      title="AI 模型设置"
      width="520px"
      :close-on-click-modal="false"
    >
      <el-form :model="settingsForm" label-width="100px">
        <el-form-item label="API 地址">
          <el-input
            v-model="settingsForm.baseUrl"
            placeholder="https://api.example.com/v1/"
          />
        </el-form-item>
        <el-form-item label="API 密钥">
          <el-input
            v-model="settingsForm.apiKey"
            type="password"
            show-password
            placeholder="输入 API 密钥"
          />
        </el-form-item>
        <el-form-item label="模型名称">
          <el-input
            v-model="settingsForm.model"
            placeholder="如: doubao-1.5-pro-32k"
          />
        </el-form-item>
        <el-form-item label="温度参数">
          <el-slider
            v-model="settingsForm.temperature"
            :min="0"
            :max="2"
            :step="0.1"
            show-input
          />
        </el-form-item>
        <el-form-item label="超时时间">
          <el-input-number
            v-model="settingsForm.timeout"
            :min="5000"
            :step="10000"
            :max="600000"
          />
          <span class="timeout-unit">毫秒</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="settingsVisible = false">取消</el-button>
        <el-button type="primary" @click="saveSettings">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.ai-chat-page {
  width: 100%;
  height: 100%;
  padding: 20px;
  background: #f0f2f5;
  box-sizing: border-box;
  overflow: hidden;
}

.chat-layout {
  display: flex;
  gap: 20px;
  height: 100%;
}

.chat-main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  border-bottom: 1px solid #f0f0f0;
  flex-shrink: 0;
}

.header-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 700;
  color: #1a1a2e;
  margin: 0;
}

.header-desc {
  font-size: 12px;
  color: #909399;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.chat-welcome {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  text-align: center;
  color: #909399;
  gap: 12px;
}

.welcome-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: #ecf5ff;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #409eff;
}

.chat-welcome h3 {
  font-size: 18px;
  color: #303133;
  margin: 0;
}

.welcome-text {
  font-size: 14px;
  color: #909399;
  margin: 0;
  max-width: 400px;
}

.welcome-suggestions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
  margin-top: 8px;
}

.message-row {
  display: flex;
  gap: 12px;
  max-width: 80%;
}

.message-user {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.message-ai {
  align-self: flex-start;
}

.message-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.message-user .message-avatar {
  background: #409eff;
  color: #fff;
}

.message-ai .message-avatar {
  background: #ecf5ff;
  color: #409eff;
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
  white-space: pre-wrap;
}

.bubble-user {
  background: #409eff;
  color: #fff;
  border-bottom-right-radius: 4px;
}

.bubble-ai {
  background: #f5f7fa;
  color: #303133;
  border-bottom-left-radius: 4px;
}

.message-content {
  min-height: 1.2em;
}

.chat-input-area {
  padding: 16px 24px;
  border-top: 1px solid #f0f0f0;
  flex-shrink: 0;
}

.input-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 8px;
}

.input-hint {
  font-size: 12px;
  color: #c0c4cc;
}

.chat-sidebar {
  width: 280px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.sidebar-section {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.sidebar-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 16px;
}

.doc-count {
  font-weight: 400;
  color: #909399;
  font-size: 13px;
}

.upload-icon {
  color: #409eff;
  margin-bottom: 8px;
}

.upload-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 13px;
  color: #606266;
}

.upload-hint {
  font-size: 12px;
  color: #c0c4cc;
}

.sidebar-empty {
  text-align: center;
  padding: 20px 0;
  color: #c0c4cc;
  font-size: 13px;
}

.sidebar-empty p {
  margin: 0;
}

.doc-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.doc-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 10px;
  border-radius: 8px;
  background: #f5f7fa;
  transition: background 0.2s;
}

.doc-item:hover {
  background: #ecf5ff;
}

.doc-info {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
  flex: 1;
}

.doc-meta {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.doc-name {
  font-size: 13px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.doc-size {
  font-size: 11px;
  color: #c0c4cc;
}

.timeout-unit {
  margin-left: 8px;
  font-size: 13px;
  color: #909399;
}
</style>
