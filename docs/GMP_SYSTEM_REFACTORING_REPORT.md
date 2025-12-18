# GMP信息管理系统重构报告

## 📋 **报告概述**

**报告日期**: 2025年12月16日
**报告版本**: v1.0
**编制人员**: GMP系统架构团队
**审核状态**: 待审核

---

## 🎯 **重构目标**

### **核心目标**
1. **引入NL2SQL中间件**：降低数据库操作技术门槛，提升开发效率
2. **全面AI集成**：实现柔性配置，支持业务规则动态调整
3. **优化用户体验**：提供智能化的前端界面和交互
4. **提升系统可维护性**：简化架构，增强可扩展性

### **成功指标**
- ✅ NL2SQL查询响应时间 < 2秒
- ✅ AI推荐准确率 > 85%
- ✅ 系统配置变更时间 < 10分钟
- ✅ 前端页面加载时间 < 3秒

---

## 🏗️ **架构重构方案**

### **1. 当前架构分析**

```
现有架构：
┌─────────────────────────────────────┐
│         前端层 (HTML + JS)          │
├─────────────────────────────────────┤
│       网关层 (Spring Gateway)       │
├─────────────────────────────────────┤
│   业务服务层 (13个微服务)           │
│   ├── QMS, MES, LIMS, EDMS...      │
├─────────────────────────────────────┤
│     数据访问层 (JPA + PostgreSQL)   │
└─────────────────────────────────────┘
```

**问题识别**：
- ❌ 数据库查询复杂，业务代码耦合度高
- ❌ AI功能规划完善，但实际落地不足
- ❌ 前端界面基础，智能化程度低
- ❌ 配置管理硬编码，缺乏柔性

### **2. 重构后架构**

```
重构后架构：
┌─────────────────────────────────────┐
│         前端层 (Vue3 + AI组件)      │
│   ├── 智能查询界面                  │
│   ├── AI推荐面板                    │
│   └── 动态配置界面                  │
├─────────────────────────────────────┤
│       网关层 (增强版Gateway)        │
│   ├── AI路由策略                    │
│   └── 智能负载均衡                  │
├─────────────────────────────────────┤
│   业务服务层 (重构微服务)           │
│   ├── NL2SQL服务 (新增)             │
│   ├── AI集成服务 (增强)             │
│   ├── 配置管理服务 (增强)           │
│   └── 业务服务 (优化)               │
├─────────────────────────────────────┤
│   数据访问层 (智能数据层)           │
│   ├── NL2SQL引擎                    │
│   ├── AI缓存层                      │
│   ├── 动态ORM层                     │
│   └── PostgreSQL + Redis            │
└─────────────────────────────────────┘
```

---

## 🔧 **后端接口设计**

### **1. NL2SQL服务接口**

#### **1.1 自然语言查询接口**

```java
@RestController
@RequestMapping("/api/nl2sql")
public class NL2SQLController {

    @Autowired
    private NL2SQLService nl2SQLService;

    /**
     * 自然语言转SQL查询
     */
    @PostMapping("/query")
    public ResponseEntity<NL2SQLResponse> naturalLanguageQuery(
            @RequestBody NL2SQLRequest request,
            @RequestHeader("Authorization") String token) {

        // 1. 解析用户权限，确定数据访问范围
        UserContext userContext = authService.parseToken(token);
        DataScope dataScope = permissionService.getDataScope(userContext);

        // 2. 自然语言转SQL
        String sql = nl2SQLService.convertToSQL(request.getQuery(), dataScope);

        // 3. 执行查询并返回结果
        QueryResult result = nl2SQLService.executeQuery(sql, request.getParams());

        // 4. AI优化结果展示
        AIEnhancedResult aiResult = aiService.enhanceResult(result, request.getQuery());

        return ResponseEntity.ok(NL2SQLResponse.success(aiResult));
    }

    /**
     * 获取查询建议
     */
    @GetMapping("/suggestions")
    public ResponseEntity<List<String>> getQuerySuggestions(
            @RequestParam String partialQuery) {

        List<String> suggestions = nl2SQLService.generateSuggestions(partialQuery);
        return ResponseEntity.ok(suggestions);
    }
}
```

**接口详情**：

| 接口路径 | 方法 | 功能描述 | 请求体 | 响应体 |
|---------|------|----------|--------|--------|
| `/api/nl2sql/query` | POST | 自然语言查询 | `NL2SQLRequest` | `NL2SQLResponse` |
| `/api/nl2sql/suggestions` | GET | 查询建议 | - | `List<String>` |
| `/api/nl2sql/history` | GET | 查询历史 | - | `Page<QueryHistory>` |
| `/api/nl2sql/templates` | GET | 查询模板 | - | `List<QueryTemplate>` |

#### **1.2 请求/响应模型**

```java
// 请求模型
public class NL2SQLRequest {
    private String query;        // 自然语言查询
    private Map<String, Object> context;  // 查询上下文
    private QueryConfig config;  // 查询配置

    // Getters and Setters...
}

// 响应模型
public class NL2SQLResponse {
    private boolean success;
    private String sql;          // 生成的SQL
    private QueryResult data;    // 查询结果
    private AIInsights insights; // AI分析洞察
    private List<String> suggestions; // 相关查询建议

    // Getters and Setters...
}
```

### **2. AI集成服务接口**

#### **2.1 智能推荐接口**

```java
@RestController
@RequestMapping("/api/ai/recommendation")
public class AIRecommendationController {

    @Autowired
    private AIRecommendationService recommendationService;

    /**
     * 获取个性化推荐
     */
    @GetMapping("/personalized/{userId}")
    public ResponseEntity<RecommendationResponse> getPersonalizedRecommendations(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {

        List<Recommendation> recommendations =
            recommendationService.getPersonalizedRecommendations(userId, limit);

        return ResponseEntity.ok(RecommendationResponse.success(recommendations));
    }

    /**
     * 基于上下文的智能推荐
     */
    @PostMapping("/contextual")
    public ResponseEntity<RecommendationResponse> getContextualRecommendations(
            @RequestBody RecommendationContext context) {

        // 分析用户当前操作上下文
        List<Recommendation> recommendations =
            recommendationService.analyzeContextAndRecommend(context);

        return ResponseEntity.ok(RecommendationResponse.success(recommendations));
    }

    /**
     * 质量问题预测
     */
    @PostMapping("/quality/predict")
    public ResponseEntity<PredictionResponse> predictQualityIssues(
            @RequestBody QualityPredictionRequest request) {

        QualityPredictionResult result =
            qualityPredictionService.predictIssues(request);

        return ResponseEntity.ok(PredictionResponse.success(result));
    }
}
```

#### **2.2 动态配置接口**

```java
@RestController
@RequestMapping("/api/config/dynamic")
public class DynamicConfigController {

    @Autowired
    private DynamicConfigService configService;

    /**
     * 获取动态配置
     */
    @GetMapping("/{serviceName}")
    public ResponseEntity<ConfigResponse> getDynamicConfig(
            @PathVariable String serviceName,
            @RequestParam String version) {

        DynamicConfig config = configService.getConfig(serviceName, version);
        return ResponseEntity.ok(ConfigResponse.success(config));
    }

    /**
     * 更新动态配置
     */
    @PutMapping("/{serviceName}")
    public ResponseEntity<ConfigResponse> updateDynamicConfig(
            @PathVariable String serviceName,
            @RequestBody DynamicConfigUpdateRequest request) {

        // 验证配置合法性
        ValidationResult validation = configService.validateConfig(request);

        if (!validation.isValid()) {
            return ResponseEntity.badRequest()
                .body(ConfigResponse.error(validation.getErrors()));
        }

        // 更新配置
        DynamicConfig updatedConfig = configService.updateConfig(serviceName, request);

        // 通知相关服务重新加载配置
        configService.notifyConfigChange(serviceName, updatedConfig);

        return ResponseEntity.ok(ConfigResponse.success(updatedConfig));
    }

    /**
     * 配置版本管理
     */
    @GetMapping("/{serviceName}/versions")
    public ResponseEntity<List<ConfigVersion>> getConfigVersions(
            @PathVariable String serviceName) {

        List<ConfigVersion> versions = configService.getConfigVersions(serviceName);
        return ResponseEntity.ok(versions);
    }
}
```

### **3. 业务服务优化接口**

#### **3.1 智能质量管理接口**

```java
@RestController
@RequestMapping("/api/qms/smart")
public class SmartQMSController {

    @Autowired
    private SmartQMSService smartQmsService;

    /**
     * AI辅助偏差分析
     */
    @PostMapping("/deviation/analyze")
    public ResponseEntity<DeviationAnalysisResponse> analyzeDeviation(
            @RequestBody DeviationAnalysisRequest request) {

        // 1. NL2SQL查询历史偏差数据
        String historyQuery = "查找相似偏差案例及处理方案";
        List<HistoricalCase> similarCases = nl2SQLService
            .executeNaturalQuery(historyQuery, Map.of("deviationId", request.getDeviationId()));

        // 2. AI模型分析根本原因
        RootCauseAnalysis analysis = aiService.predict("root_cause_model",
            Map.of("deviationData", request.getDeviationData(), "similarCases", similarCases));

        // 3. 生成AI建议的纠正预防措施
        CorrectiveAction action = aiService.generate("action_recommendation_model",
            Map.of("analysis", analysis, "companyPolicies", policies));

        DeviationAnalysis result = new DeviationAnalysis(analysis, action);
        return ResponseEntity.ok(DeviationAnalysisResponse.success(result));
    }

    /**
     * 智能CAPA建议
     */
    @PostMapping("/capa/suggest")
    public ResponseEntity<CapaSuggestionResponse> suggestCAPA(
            @RequestBody CapaSuggestionRequest request) {

        // 基于历史数据和当前问题生成CAPA建议
        CapaSuggestion suggestion = smartQmsService.generateCapaSuggestion(request);

        return ResponseEntity.ok(CapaSuggestionResponse.success(suggestion));
    }
}
```

---

## 🎨 **前端设计方案**

### **1. 技术栈升级**

```javascript
// 前端技术栈
const techStack = {
    framework: "Vue 3 + Composition API",
    ui: "Element Plus + Naive UI",
    state: "Pinia",
    router: "Vue Router 4",
    http: "Axios + GraphQL",
    build: "Vite",
    ai: "自定义AI组件库"
};
```

### **2. 智能查询界面设计**

#### **2.1 自然语言查询组件**

```vue
<template>
  <div class="nl2sql-query">
    <div class="query-input-section">
      <div class="input-wrapper">
        <el-input
          v-model="query"
          placeholder="用自然语言描述您想要查询的内容..."
          :loading="loading"
          @keyup.enter="executeQuery"
          clearable
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
          <template #suffix>
            <el-button
              type="primary"
              :loading="loading"
              @click="executeQuery"
              size="small"
            >
              查询
            </el-button>
          </template>
        </el-input>
      </div>

      <!-- 查询建议 -->
      <div v-if="suggestions.length > 0" class="suggestions-panel">
        <div class="suggestion-title">建议查询：</div>
        <el-tag
          v-for="suggestion in suggestions"
          :key="suggestion"
          @click="applySuggestion(suggestion)"
          class="suggestion-tag"
        >
          {{ suggestion }}
        </el-tag>
      </div>
    </div>

    <!-- 查询结果 -->
    <div v-if="result" class="query-result">
      <div class="result-header">
        <div class="sql-preview">
          <span class="sql-label">生成的SQL：</span>
          <el-tooltip :content="result.sql" placement="top">
            <code class="sql-code">{{ truncateSQL(result.sql) }}</code>
          </el-tooltip>
        </div>
        <div class="result-actions">
          <el-button size="small" @click="showSQL = !showSQL">
            {{ showSQL ? '隐藏SQL' : '显示SQL' }}
          </el-button>
        </div>
      </div>

      <!-- AI洞察 -->
      <div v-if="result.insights" class="ai-insights">
        <h4>🤖 AI分析洞察</h4>
        <div class="insights-content">
          <el-alert
            v-for="insight in result.insights"
            :key="insight.id"
            :type="insight.type"
            :title="insight.title"
            :description="insight.description"
            show-icon
            :closable="false"
          />
        </div>
      </div>

      <!-- 数据表格 -->
      <div class="data-table">
        <el-table
          :data="result.data.rows"
          :columns="result.data.columns"
          stripe
          :loading="loading"
        >
          <el-table-column
            v-for="column in result.data.columns"
            :key="column.key"
            :prop="column.key"
            :label="column.label"
            :width="column.width"
          />
        </el-table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Search } from '@element-plus/icons-vue'

const query = ref('')
const loading = ref(false)
const showSQL = ref(false)
const suggestions = ref([])
const result = ref(null)

// 执行自然语言查询
const executeQuery = async () => {
  if (!query.value.trim()) return

  loading.value = true
  try {
    const response = await fetch('/api/nl2sql/query', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${getToken()}`
      },
      body: JSON.stringify({
        query: query.value,
        context: getCurrentContext()
      })
    })

    const data = await response.json()
    result.value = data.data

    // 获取相关建议
    await loadSuggestions(query.value)

  } catch (error) {
    console.error('查询失败:', error)
    ElMessage.error('查询失败，请重试')
  } finally {
    loading.value = false
  }
}

// 加载查询建议
const loadSuggestions = async (partialQuery) => {
  try {
    const response = await fetch(`/api/nl2sql/suggestions?partialQuery=${encodeURIComponent(partialQuery)}`)
    const data = await response.json()
    suggestions.value = data.data || []
  } catch (error) {
    console.error('加载建议失败:', error)
  }
}

// 应用建议
const applySuggestion = (suggestion) => {
  query.value = suggestion
  executeQuery()
}

// 截断SQL显示
const truncateSQL = (sql) => {
  return sql.length > 100 ? sql.substring(0, 100) + '...' : sql
}
</script>

<style scoped>
.nl2sql-query {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.query-input-section {
  margin-bottom: 30px;
}

.input-wrapper {
  position: relative;
}

.suggestions-panel {
  margin-top: 10px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.suggestion-title {
  font-weight: 600;
  color: #495057;
  margin-bottom: 10px;
}

.suggestion-tag {
  margin: 5px 5px 0 0;
  cursor: pointer;
  transition: all 0.3s ease;
}

.suggestion-tag:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

.query-result {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e9ecef;
}

.sql-preview {
  flex: 1;
}

.sql-label {
  font-weight: 600;
  color: #495057;
  margin-right: 10px;
}

.sql-code {
  background: #f8f9fa;
  padding: 4px 8px;
  border-radius: 4px;
  font-family: 'Monaco', 'Menlo', monospace;
  font-size: 14px;
  color: #e83e8c;
  cursor: help;
}

.ai-insights {
  margin-bottom: 25px;
}

.ai-insights h4 {
  color: #495057;
  margin-bottom: 15px;
  font-size: 16px;
}

.insights-content {
  display: grid;
  gap: 10px;
}

.data-table {
  margin-top: 20px;
}
</style>
```

#### **2.2 AI推荐面板组件**

```vue
<template>
  <div class="ai-recommendation-panel">
    <div class="panel-header">
      <div class="header-left">
        <el-icon class="ai-icon"><LightBulb /></el-icon>
        <span class="panel-title">AI智能推荐</span>
      </div>
      <div class="header-right">
        <el-switch
          v-model="autoRefresh"
          active-text="自动刷新"
          inactive-text="手动刷新"
          @change="toggleAutoRefresh"
        />
        <el-button
          type="primary"
          size="small"
          @click="refreshRecommendations"
          :loading="loading"
        >
          <el-icon><RefreshRight /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <div class="recommendations-container">
      <!-- 个性化推荐 -->
      <div class="recommendation-section">
        <h4 class="section-title">
          <el-icon><User /></el-icon>
          个性化推荐
        </h4>
        <div class="recommendations-grid">
          <div
            v-for="rec in personalizedRecommendations"
            :key="rec.id"
            class="recommendation-card"
            @click="handleRecommendationClick(rec)"
          >
            <div class="card-header">
              <el-tag :type="getRecommendationType(rec.type)">
                {{ getRecommendationLabel(rec.type) }}
              </el-tag>
              <span class="confidence-score">
                置信度: {{ (rec.confidence * 100).toFixed(1) }}%
              </span>
            </div>
            <div class="card-content">
              <h5 class="rec-title">{{ rec.title }}</h5>
              <p class="rec-description">{{ rec.description }}</p>
            </div>
            <div class="card-actions">
              <el-button type="text" size="small" @click.stop="acceptRecommendation(rec)">
                采纳
              </el-button>
              <el-button type="text" size="small" @click.stop="dismissRecommendation(rec)">
                忽略
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 上下文相关推荐 -->
      <div class="recommendation-section">
        <h4 class="section-title">
          <el-icon><Position /></el-icon>
          上下文推荐
        </h4>
        <div class="context-recommendations">
          <div
            v-for="rec in contextualRecommendations"
            :key="rec.id"
            class="context-rec-item"
            @click="handleContextRecommendationClick(rec)"
          >
            <div class="context-icon">
              <el-icon :size="20" :color="getContextIconColor(rec.contextType)">
                <component :is="getContextIcon(rec.contextType)" />
              </el-icon>
            </div>
            <div class="context-content">
              <div class="context-title">{{ rec.title }}</div>
              <div class="context-meta">
                <span class="context-type">{{ getContextTypeLabel(rec.contextType) }}</span>
                <span class="context-time">{{ formatTime(rec.timestamp) }}</span>
              </div>
            </div>
            <div class="context-actions">
              <el-button type="text" size="small">查看</el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 质量预测预警 -->
      <div class="recommendation-section">
        <h4 class="section-title">
          <el-icon><Warning /></el-icon>
          质量预测预警
        </h4>
        <div class="prediction-alerts">
          <el-alert
            v-for="alert in qualityPredictions"
            :key="alert.id"
            :type="getAlertType(alert.severity)"
            :title="alert.title"
            :description="alert.description"
            show-icon
            :closable="false"
          >
            <template #title>
              <div class="alert-title">
                <span>{{ alert.title }}</span>
                <el-tag size="small" :type="getSeverityTagType(alert.severity)">
                  {{ getSeverityLabel(alert.severity) }}
                </el-tag>
              </div>
            </template>
            <div class="alert-actions">
              <el-button size="small" type="primary" @click="handleAlertAction(alert)">
                查看详情
              </el-button>
              <el-button size="small" @click="dismissAlert(alert)">
                忽略
              </el-button>
            </div>
          </el-alert>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import {
  LightBulb,
  RefreshRight,
  User,
  Position,
  Warning
} from '@element-plus/icons-vue'

// 响应式数据
const personalizedRecommendations = ref([])
const contextualRecommendations = ref([])
const qualityPredictions = ref([])
const autoRefresh = ref(true)
const loading = ref(false)
let refreshInterval = null

// 获取个性化推荐
const loadPersonalizedRecommendations = async () => {
  try {
    const response = await fetch('/api/ai/recommendation/personalized/current-user?limit=6')
    const data = await response.json()

    if (data.success) {
      personalizedRecommendations.value = data.data
    }
  } catch (error) {
    console.error('加载个性化推荐失败:', error)
  }
}

// 获取上下文推荐
const loadContextualRecommendations = async () => {
  try {
    const response = await fetch('/api/ai/recommendation/contextual', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        currentPage: getCurrentPage(),
        userActions: getRecentActions(),
        contextData: getPageContext()
      })
    })

    const data = await response.json()
    if (data.success) {
      contextualRecommendations.value = data.data
    }
  } catch (error) {
    console.error('加载上下文推荐失败:', error)
  }
}

// 获取质量预测
const loadQualityPredictions = async () => {
  try {
    const response = await fetch('/api/ai/recommendation/quality/predict', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        timeRange: '7d',
        predictionTypes: ['deviation', 'equipment_failure', 'quality_issue']
      })
    })

    const data = await response.json()
    if (data.success) {
      qualityPredictions.value = data.data.filter(alert => alert.severity >= 3)
    }
  } catch (error) {
    console.error('加载质量预测失败:', error)
  }
}

// 刷新推荐
const refreshRecommendations = async () => {
  loading.value = true
  await Promise.all([
    loadPersonalizedRecommendations(),
    loadContextualRecommendations(),
    loadQualityPredictions()
  ])
  loading.value = false
}

// 切换自动刷新
const toggleAutoRefresh = (enabled) => {
  if (enabled) {
    refreshInterval = setInterval(refreshRecommendations, 30000) // 30秒刷新一次
  } else {
    if (refreshInterval) {
      clearInterval(refreshInterval)
      refreshInterval = null
    }
  }
}

// 处理推荐点击
const handleRecommendationClick = (recommendation) => {
  // 记录用户行为
  trackUserInteraction('recommendation_click', recommendation)

  // 导航到相关页面或执行操作
  navigateToRecommendation(recommendation)
}

// 采纳推荐
const acceptRecommendation = async (recommendation) => {
  try {
    await fetch(`/api/ai/recommendation/${recommendation.id}/accept`, {
      method: 'POST'
    })

    // 从列表中移除
    const index = personalizedRecommendations.value.findIndex(r => r.id === recommendation.id)
    if (index > -1) {
      personalizedRecommendations.value.splice(index, 1)
    }

    ElMessage.success('已采纳推荐')
  } catch (error) {
    console.error('采纳推荐失败:', error)
    ElMessage.error('操作失败，请重试')
  }
}

// 忽略推荐
const dismissRecommendation = async (recommendation) => {
  try {
    await fetch(`/api/ai/recommendation/${recommendation.id}/dismiss`, {
      method: 'POST'
    })

    // 从列表中移除
    const index = personalizedRecommendations.value.findIndex(r => r.id === recommendation.id)
    if (index > -1) {
      personalizedRecommendations.value.splice(index, 1)
    }
  } catch (error) {
    console.error('忽略推荐失败:', error)
  }
}

// 工具函数
const getRecommendationType = (type) => {
  const typeMap = {
    'task': 'info',
    'document': 'success',
    'action': 'warning',
    'alert': 'danger'
  }
  return typeMap[type] || 'info'
}

const getRecommendationLabel = (type) => {
  const labelMap = {
    'task': '任务',
    'document': '文档',
    'action': '操作',
    'alert': '提醒'
  }
  return labelMap[type] || type
}

const getContextIcon = (contextType) => {
  // 返回对应的图标组件
  return 'Document'
}

const getContextIconColor = (contextType) => {
  const colorMap = {
    'page': '#409EFF',
    'action': '#67C23A',
    'search': '#E6A23C'
  }
  return colorMap[contextType] || '#909399'
}

const getContextTypeLabel = (contextType) => {
  const labelMap = {
    'page': '页面',
    'action': '操作',
    'search': '搜索'
  }
  return labelMap[contextType] || contextType
}

const getAlertType = (severity) => {
  return severity >= 4 ? 'error' : severity >= 3 ? 'warning' : 'info'
}

const getSeverityTagType = (severity) => {
  return severity >= 4 ? 'danger' : severity >= 3 ? 'warning' : 'info'
}

const getSeverityLabel = (severity) => {
  const labels = ['低', '中', '高', '严重', '紧急']
  return labels[severity - 1] || '未知'
}

const formatTime = (timestamp) => {
  return new Date(timestamp).toLocaleTimeString()
}

// 生命周期
onMounted(() => {
  refreshRecommendations()
  if (autoRefresh.value) {
    toggleAutoRefresh(true)
  }
})

onUnmounted(() => {
  if (refreshInterval) {
    clearInterval(refreshInterval)
  }
})
</script>

<style scoped>
.ai-recommendation-panel {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #ebeef5;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.ai-icon {
  color: #409eff;
  font-size: 20px;
}

.panel-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.recommendations-container {
  padding: 24px;
}

.recommendation-section {
  margin-bottom: 32px;
}

.recommendation-section:last-child {
  margin-bottom: 0;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 16px;
}

.recommendations-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.recommendation-card {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #fafbfc;
}

.recommendation-card:hover {
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
  transform: translateY(-2px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.confidence-score {
  font-size: 12px;
  color: #909399;
}

.card-content {
  margin-bottom: 12px;
}

.rec-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.rec-description {
  font-size: 13px;
  color: #606266;
  line-height: 1.4;
}

.card-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.context-recommendations {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.context-rec-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.context-rec-item:hover {
  border-color: #67c23a;
  background: #f0f9ff;
}

.context-icon {
  flex-shrink: 0;
}

.context-content {
  flex: 1;
}

.context-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.context-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: #909399;
}

.context-actions {
  flex-shrink: 0;
}

.prediction-alerts {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.alert-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}

.alert-actions {
  margin-top: 12px;
  display: flex;
  gap: 8px;
}
</style>
```

### **3. 动态配置界面设计**

```vue
<template>
  <div class="dynamic-config-panel">
    <div class="panel-header">
      <div class="header-content">
        <el-icon class="config-icon"><Setting /></el-icon>
        <div class="header-text">
          <h3 class="panel-title">动态配置管理</h3>
          <p class="panel-subtitle">实时调整系统行为，无需重启服务</p>
        </div>
      </div>
      <div class="header-actions">
        <el-button
          type="primary"
          @click="openAddConfigDialog"
          :icon="Plus"
        >
          新增配置
        </el-button>
      </div>
    </div>

    <div class="config-content">
      <!-- 配置树形结构 -->
      <div class="config-tree-section">
        <div class="section-header">
          <h4>配置结构</h4>
          <div class="tree-actions">
            <el-input
              v-model="searchQuery"
              placeholder="搜索配置项..."
              size="small"
              prefix-icon="Search"
              @input="filterTree"
              style="width: 200px"
            />
            <el-button size="small" @click="expandAll">展开全部</el-button>
            <el-button size="small" @click="collapseAll">收起全部</el-button>
          </div>
        </div>

        <div class="config-tree">
          <el-tree
            ref="configTreeRef"
            :data="configTreeData"
            :props="treeProps"
            :filter-node-method="filterNode"
            @node-click="handleNodeClick"
            node-key="id"
            default-expand-all
          >
            <template #default="{ node, data }">
              <div class="tree-node-content">
                <div class="node-info">
                  <el-icon class="node-icon">
                    <component :is="getNodeIcon(data.type)" />
                  </el-icon>
                  <span class="node-label">{{ node.label }}</span>
                  <el-tag
                    v-if="data.status"
                    :type="getStatusType(data.status)"
                    size="small"
                    class="node-status"
                  >
                    {{ getStatusLabel(data.status) }}
                  </el-tag>
                </div>
                <div class="node-actions">
                  <el-button
                    v-if="data.type === 'service'"
                    type="text"
                    size="small"
                    @click.stop="viewServiceConfig(data)"
                  >
                    查看配置
                  </el-button>
                  <el-button
                    type="text"
                    size="small"
                    @click.stop="editConfig(data)"
                  >
                    编辑
                  </el-button>
                </div>
              </div>
            </template>
          </el-tree>
        </div>
      </div>

      <!-- 配置详情面板 -->
      <div class="config-detail-section">
        <div v-if="!selectedConfig" class="no-selection">
          <el-empty description="请选择配置项查看详情">
            <el-button type="primary" @click="openAddConfigDialog">
              创建配置
            </el-button>
          </el-empty>
        </div>

        <div v-else class="config-detail">
          <div class="detail-header">
            <div class="detail-info">
              <h4 class="config-name">{{ selectedConfig.name }}</h4>
              <p class="config-description">{{ selectedConfig.description }}</p>
              <div class="config-meta">
                <el-tag :type="getTypeTagType(selectedConfig.type)">
                  {{ getTypeLabel(selectedConfig.type) }}
                </el-tag>
                <span class="config-version">版本: {{ selectedConfig.version }}</span>
                <span class="config-updated">更新时间: {{ formatDate(selectedConfig.updatedAt) }}</span>
              </div>
            </div>
            <div class="detail-actions">
              <el-button @click="editConfig(selectedConfig)">编辑配置</el-button>
              <el-button type="danger" @click="deleteConfig(selectedConfig)">删除配置</el-button>
            </div>
          </div>

          <!-- 配置内容 -->
          <div class="config-content-editor">
            <div class="editor-tabs">
              <el-tabs v-model="activeTab" @tab-click="handleTabClick">
                <el-tab-pane label="配置内容" name="content">
                  <div class="json-editor">
                    <json-editor
                      v-model="configContent"
                      :mode="'code'"
                      :read-only="false"
                      @change="handleConfigChange"
                    />
                  </div>
                </el-tab-pane>

                <el-tab-pane label="版本历史" name="history">
                  <div class="version-history">
                    <el-timeline>
                      <el-timeline-item
                        v-for="version in configVersions"
                        :key="version.id"
                        :timestamp="formatDate(version.createdAt)"
                      >
                        <div class="version-info">
                          <div class="version-header">
                            <span class="version-number">版本 {{ version.version }}</span>
                            <el-tag size="small" :type="version.status === 'active' ? 'success' : 'info'">
                              {{ version.status === 'active' ? '当前版本' : '历史版本' }}
                            </el-tag>
                          </div>
                          <p class="version-description">{{ version.description }}</p>
                          <div class="version-actions">
                            <el-button size="small" @click="rollbackToVersion(version)">
                              回滚到此版本
                            </el-button>
                            <el-button size="small" type="text" @click="viewVersionDiff(version)">
                              查看差异
                            </el-button>
                          </div>
                        </div>
                      </el-timeline-item>
                    </el-timeline>
                  </div>
                </el-tab-pane>

                <el-tab-pane label="生效状态" name="status">
                  <div class="deployment-status">
                    <div class="status-overview">
                      <div class="status-card">
                        <div class="status-icon">
                          <el-icon :class="deploymentStatus.overall === 'success' ? 'success' : 'warning'">
                            <CheckCircle v-if="deploymentStatus.overall === 'success'" />
                            <Warning v-else />
                          </el-icon>
                        </div>
                        <div class="status-info">
                          <h5>整体状态</h5>
                          <p>{{ getDeploymentStatusText(deploymentStatus.overall) }}</p>
                        </div>
                      </div>
                    </div>

                    <div class="service-status">
                      <h5>服务生效状态</h5>
                      <div class="service-list">
                        <div
                          v-for="service in deploymentStatus.services"
                          :key="service.name"
                          class="service-item"
                        >
                          <div class="service-name">{{ service.name }}</div>
                          <el-tag :type="service.status === 'success' ? 'success' : 'warning'">
                            {{ getServiceStatusText(service.status) }}
                          </el-tag>
                          <div class="service-time">{{ formatDate(service.updatedAt) }}</div>
                        </div>
                      </div>
                    </div>
                  </div>
                </el-tab-pane>
              </el-tabs>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 新增配置对话框 -->
    <el-dialog
      v-model="addConfigDialogVisible"
      title="新增配置"
      width="600px"
    >
      <el-form
        ref="addConfigFormRef"
        :model="addConfigForm"
        :rules="addConfigRules"
        label-width="100px"
      >
        <el-form-item label="配置名称" prop="name">
          <el-input v-model="addConfigForm.name" placeholder="输入配置名称" />
        </el-form-item>

        <el-form-item label="配置类型" prop="type">
          <el-select v-model="addConfigForm.type" placeholder="选择配置类型">
            <el-option label="服务配置" value="service" />
            <el-option label="业务规则" value="rule" />
            <el-option label="UI配置" value="ui" />
            <el-option label="AI模型配置" value="ai" />
          </el-select>
        </el-form-item>

        <el-form-item label="服务名称" prop="serviceName" v-if="addConfigForm.type === 'service'">
          <el-select v-model="addConfigForm.serviceName" placeholder="选择目标服务">
            <el-option
              v-for="service in availableServices"
              :key="service.name"
              :label="service.label"
              :value="service.name"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="配置描述" prop="description">
          <el-input
            v-model="addConfigForm.description"
            type="textarea"
            :rows="3"
            placeholder="输入配置描述"
          />
        </el-form-item>

        <el-form-item label="初始配置" prop="content">
          <json-editor
            v-model="addConfigForm.content"
            :mode="'code'"
            :read-only="false"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="addConfigDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAddConfig" :loading="submitting">
          创建配置
        </el-button>
      </template>
    </el-dialog>

    <!-- 编辑配置对话框 -->
    <el-dialog
      v-model="editConfigDialogVisible"
      :title="`编辑配置 - ${editingConfig?.name || ''}`"
      width="800px"
      :before-close="handleEditDialogClose"
    >
      <div class="edit-config-content">
        <json-editor
          v-model="editingConfigContent"
          :mode="'code'"
          :read-only="false"
          @change="handleEditingConfigChange"
        />
      </div>

      <template #footer>
        <el-button @click="cancelEditConfig">取消</el-button>
        <el-button type="primary" @click="saveEditConfig" :loading="saving">
          保存更改
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import {
  Setting,
  Plus,
  Search,
  CheckCircle,
  Warning
} from '@element-plus/icons-vue'
import JsonEditor from '@/components/JsonEditor.vue'

// 响应式数据
const configTreeData = ref([])
const selectedConfig = ref(null)
const configContent = ref('')
const configVersions = ref([])
const deploymentStatus = ref({})
const searchQuery = ref('')
const activeTab = ref('content')
const addConfigDialogVisible = ref(false)
const editConfigDialogVisible = ref(false)
const submitting = ref(false)
const saving = ref(false)

// 表单数据
const addConfigForm = reactive({
  name: '',
  type: '',
  serviceName: '',
  description: '',
  content: '{}'
})

const editingConfig = ref(null)
const editingConfigContent = ref('')
const availableServices = ref([
  { name: 'qms-service', label: '质量管理系统' },
  { name: 'mes-service', label: '生产执行系统' },
  { name: 'lims-service', label: '实验室信息系统' },
  { name: 'ai-service', label: 'AI智能服务' }
])

// 表单验证规则
const addConfigRules = {
  name: [
    { required: true, message: '请输入配置名称', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择配置类型', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入配置描述', trigger: 'blur' }
  ]
}

// 树形结构配置
const treeProps = {
  children: 'children',
  label: 'label'
}

// 加载配置树
const loadConfigTree = async () => {
  try {
    const response = await fetch('/api/config/dynamic/tree')
    const data = await response.json()

    if (data.success) {
      configTreeData.value = data.data
    }
  } catch (error) {
    console.error('加载配置树失败:', error)
    ElMessage.error('加载配置树失败')
  }
}

// 过滤树节点
const filterNode = (value, data) => {
  if (!value) return true
  return data.label.toLowerCase().includes(value.toLowerCase())
}

// 过滤树
const filterTree = () => {
  configTreeRef.value?.filter(searchQuery.value)
}

// 展开全部
const expandAll = () => {
  configTreeRef.value?.expandAll()
}

// 收起全部
const collapseAll = () => {
  configTreeRef.value?.collapseAll()
}

// 处理节点点击
const handleNodeClick = async (data) => {
  if (data.type === 'config') {
    await loadConfigDetail(data.id)
  }
}

// 加载配置详情
const loadConfigDetail = async (configId) => {
  try {
    const [configResponse, versionsResponse, statusResponse] = await Promise.all([
      fetch(`/api/config/dynamic/config/${configId}`),
      fetch(`/api/config/dynamic/config/${configId}/versions`),
      fetch(`/api/config/dynamic/config/${configId}/status`)
    ])

    const [configData, versionsData, statusData] = await Promise.all([
      configResponse.json(),
      versionsResponse.json(),
      statusResponse.json()
    ])

    if (configData.success) {
      selectedConfig.value = configData.data
      configContent.value = JSON.stringify(configData.data.content, null, 2)
    }

    if (versionsData.success) {
      configVersions.value = versionsData.data
    }

    if (statusData.success) {
      deploymentStatus.value = statusData.data
    }
  } catch (error) {
    console.error('加载配置详情失败:', error)
    ElMessage.error('加载配置详情失败')
  }
}

// 查看服务配置
const viewServiceConfig = (serviceData) => {
  // 导航到服务配置页面
  router.push(`/config/service/${serviceData.name}`)
}

// 编辑配置
const editConfig = (configData) => {
  editingConfig.value = configData
  editingConfigContent.value = JSON.stringify(configData.content, null, 2)
  editConfigDialogVisible.value = true
}

// 删除配置
const deleteConfig = async (configData) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除配置 "${configData.name}" 吗？此操作不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const response = await fetch(`/api/config/dynamic/config/${configData.id}`, {
      method: 'DELETE'
    })

    if (response.ok) {
      ElMessage.success('配置删除成功')
      await loadConfigTree()
      selectedConfig.value = null
    } else {
      throw new Error('删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除配置失败:', error)
      ElMessage.error('删除配置失败')
    }
  }
}

// 打开新增配置对话框
const openAddConfigDialog = () => {
  addConfigDialogVisible.value = true
  Object.assign(addConfigForm, {
    name: '',
    type: '',
    serviceName: '',
    description: '',
    content: '{}'
  })
}

// 提交新增配置
const submitAddConfig = async () => {
  try {
    await addConfigFormRef.value.validate()

    submitting.value = true

    const response = await fetch('/api/config/dynamic/config', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        ...addConfigForm,
        content: JSON.parse(addConfigForm.content)
      })
    })

    if (response.ok) {
      ElMessage.success('配置创建成功')
      addConfigDialogVisible.value = false
      await loadConfigTree()
    } else {
      throw new Error('创建失败')
    }
  } catch (error) {
    console.error('创建配置失败:', error)
    ElMessage.error('创建配置失败，请检查输入')
  } finally {
    submitting.value = false
  }
}

// 保存编辑配置
const saveEditConfig = async () => {
  try {
    saving.value = true

    const response = await fetch(`/api/config/dynamic/config/${editingConfig.value.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        ...editingConfig.value,
        content: JSON.parse(editingConfigContent.value)
      })
    })

    if (response.ok) {
      ElMessage.success('配置保存成功')
      editConfigDialogVisible.value = false
      await loadConfigDetail(editingConfig.value.id)
    } else {
      throw new Error('保存失败')
    }
  } catch (error) {
    console.error('保存配置失败:', error)
    ElMessage.error('保存配置失败，请检查配置格式')
  } finally {
    saving.value = false
  }
}

// 取消编辑配置
const cancelEditConfig = () => {
  if (hasUnsavedChanges()) {
    ElMessageBox.confirm(
      '配置有未保存的更改，确定要放弃吗？',
      '确认放弃',
      {
        confirmButtonText: '确定放弃',
        cancelButtonText: '继续编辑',
        type: 'warning'
      }
    ).then(() => {
      editConfigDialogVisible.value = false
    })
  } else {
    editConfigDialogVisible.value = false
  }
}

// 处理编辑对话框关闭
const handleEditDialogClose = (done) => {
  if (hasUnsavedChanges()) {
    ElMessageBox.confirm(
      '配置有未保存的更改，确定要关闭吗？',
      '确认关闭',
      {
        confirmButtonText: '确定关闭',
        cancelButtonText: '继续编辑',
        type: 'warning'
      }
    ).then(() => {
      done()
    })
  } else {
    done()
  }
}

// 检查是否有未保存的更改
const hasUnsavedChanges = () => {
  if (!editingConfig.value) return false

  const originalContent = JSON.stringify(editingConfig.value.content, null, 2)
  return originalContent !== editingConfigContent.value
}

// 回滚到指定版本
const rollbackToVersion = async (version) => {
  try {
    await ElMessageBox.confirm(
      `确定要回滚到版本 ${version.version} 吗？`,
      '确认回滚',
      {
        confirmButtonText: '确定回滚',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const response = await fetch(`/api/config/dynamic/config/${selectedConfig.value.id}/rollback`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ versionId: version.id })
    })

    if (response.ok) {
      ElMessage.success('配置回滚成功')
      await loadConfigDetail(selectedConfig.value.id)
    } else {
      throw new Error('回滚失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('回滚配置失败:', error)
      ElMessage.error('回滚配置失败')
    }
  }
}

// 查看版本差异
const viewVersionDiff = (version) => {
  // 打开版本差异对话框或页面
  console.log('查看版本差异:', version)
}

// 工具函数
const getNodeIcon = (type) => {
  const iconMap = {
    'group': 'Folder',
    'service': 'Grid',
    'config': 'Document'
  }
  return iconMap[type] || 'Document'
}

const getStatusType = (status) => {
  const typeMap = {
    'active': 'success',
    'inactive': 'info',
    'error': 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusLabel = (status) => {
  const labelMap = {
    'active': '活跃',
    'inactive': '停用',
    'error': '错误'
  }
  return labelMap[status] || status
}

const getTypeTagType = (type) => {
  const typeMap = {
    'service': 'primary',
    'rule': 'success',
    'ui': 'warning',
    'ai': 'danger'
  }
  return typeMap[type] || 'info'
}

const getTypeLabel = (type) => {
  const labelMap = {
    'service': '服务配置',
    'rule': '业务规则',
    'ui': 'UI配置',
    'ai': 'AI配置'
  }
  return labelMap[type] || type
}

const formatDate = (date) => {
  return new Date(date).toLocaleString()
}

const getDeploymentStatusText = (status) => {
  const textMap = {
    'success': '全部生效',
    'partial': '部分生效',
    'failed': '生效失败'
  }
  return textMap[status] || '未知状态'
}

const getServiceStatusText = (status) => {
  const textMap = {
    'success': '已生效',
    'pending': '待生效',
    'failed': '生效失败'
  }
  return textMap[status] || '未知状态'
}

const handleTabClick = (tab) => {
  // 处理标签页切换
  console.log('切换到标签:', tab.props.name)
}

const handleConfigChange = (value) => {
  // 处理配置内容变更
  console.log('配置内容变更:', value)
}

const handleEditingConfigChange = (value) => {
  // 处理编辑中的配置变更
  editingConfigContent.value = value
}

// 生命周期
onMounted(() => {
  loadConfigTree()
})
</script>

<style scoped>
.dynamic-config-panel {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid #ebeef5;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.header-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.config-icon {
  color: #409eff;
  font-size: 24px;
}

.header-text h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.header-text p {
  margin: 4px 0 0 0;
  font-size: 14px;
  color: #909399;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.config-content {
  display: grid;
  grid-template-columns: 320px 1fr;
  height: calc(100vh - 200px);
}

.config-tree-section {
  border-right: 1px solid #ebeef5;
  display: flex;
  flex-direction: column;
}

.section-header {
  padding: 16px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-header h4 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.tree-actions {
  display: flex;
  gap: 8px;
}

.config-tree {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.tree-node-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding: 4px 0;
}

.node-info {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.node-icon {
  color: #909399;
  font-size: 16px;
}

.node-label {
  font-size: 14px;
  color: #303133;
}

.node-status {
  margin-left: auto;
}

.node-actions {
  opacity: 0;
  transition: opacity 0.3s ease;
}

.tree-node-content:hover .node-actions {
  opacity: 1;
}

.config-detail-section {
  display: flex;
  flex-direction: column;
}

.no-selection {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.config-detail {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.detail-header {
  padding: 20px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.detail-info {
  flex: 1;
}

.config-name {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.config-description {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.4;
}

.config-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: #909399;
}

.detail-actions {
  display: flex;
  gap: 8px;
}

.config-content-editor {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.editor-tabs {
  flex: 1;
  display: flex;
  flex-direction: column;
}

:deep(.el-tabs__content) {
  flex: 1;
  padding: 0;
}

:deep(.el-tab-pane) {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.json-editor {
  flex: 1;
  padding: 16px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  margin: 16px;
  margin-top: 0;
}

.version-history {
  padding: 16px;
  height: 100%;
  overflow-y: auto;
}

.version-info {
  padding: 12px;
  background: #fafbfc;
  border-radius: 6px;
  border: 1px solid #ebeef5;
}

.version-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.version-number {
  font-weight: 600;
  color: #303133;
}

.version-description {
  margin: 8px 0;
  font-size: 14px;
  color: #606266;
}

.version-actions {
  margin-top: 12px;
  display: flex;
  gap: 8px;
}

.deployment-status {
  padding: 16px;
  height: 100%;
  overflow-y: auto;
}

.status-overview {
  margin-bottom: 24px;
}

.status-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.status-icon {
  font-size: 24px;
}

.status-info h5 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.status-info p {
  margin: 0;
  font-size: 14px;
  color: #606266;
}

.service-status h5 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.service-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.service-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  background: #fafbfc;
  border-radius: 6px;
  border: 1px solid #ebeef5;
}

.service-name {
  font-weight: 500;
  color: #303133;
}

.service-time {
  font-size: 12px;
  color: #909399;
}
</style>
```

---

## 🗄️ **数据库重构方案**

### **1. 引入NL2SQL中间件的数据模型**

```sql
-- NL2SQL查询历史表
CREATE TABLE nl2sql_queries (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    natural_query TEXT NOT NULL,
    generated_sql TEXT NOT NULL,
    execution_time INTEGER, -- 执行时间(毫秒)
    result_count INTEGER,   -- 结果行数
    success BOOLEAN DEFAULT TRUE,
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_created (user_id, created_at),
    INDEX idx_query_hash (MD5(natural_query))
);

-- 查询模板表
CREATE TABLE query_templates (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    template_query TEXT NOT NULL,
    parameters JSONB,
    popularity INTEGER DEFAULT 0,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- AI洞察缓存表
CREATE TABLE ai_insights_cache (
    id BIGSERIAL PRIMARY KEY,
    query_hash VARCHAR(64) NOT NULL UNIQUE,
    insights JSONB,
    confidence_score DECIMAL(3,2),
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    INDEX idx_query_hash (query_hash),
    INDEX idx_expires_at (expires_at)
);
```

### **2. 动态配置数据模型**

```sql
-- 动态配置表
CREATE TABLE dynamic_configs (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL UNIQUE,
    type VARCHAR(50) NOT NULL, -- service, rule, ui, ai
    service_name VARCHAR(100), -- 当type=service时必填
    description TEXT,
    content JSONB NOT NULL,
    version VARCHAR(20) NOT NULL DEFAULT '1.0',
    status VARCHAR(20) DEFAULT 'draft', -- draft, active, inactive
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 配置版本历史表
CREATE TABLE config_versions (
    id BIGSERIAL PRIMARY KEY,
    config_id BIGINT NOT NULL REFERENCES dynamic_configs(id),
    version VARCHAR(20) NOT NULL,
    content JSONB NOT NULL,
    description TEXT,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(config_id, version)
);

-- 配置部署状态表
CREATE TABLE config_deployments (
    id BIGSERIAL PRIMARY KEY,
    config_id BIGINT NOT NULL REFERENCES dynamic_configs(id),
    service_instance VARCHAR(200) NOT NULL,
    status VARCHAR(20) NOT NULL, -- pending, success, failed
    error_message TEXT,
    deployed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_config_service (config_id, service_instance),
    INDEX idx_status_deployed (status, deployed_at)
);
```

### **3. AI增强功能数据模型**

```sql
-- AI推荐记录表
CREATE TABLE ai_recommendations (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL, -- personalized, contextual, quality_prediction
    content JSONB NOT NULL,
    confidence_score DECIMAL(3,2),
    status VARCHAR(20) DEFAULT 'active', -- active, accepted, dismissed
    action_taken VARCHAR(100), -- 用户执行的操作
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    INDEX idx_user_type_created (user_id, type, created_at),
    INDEX idx_status_expires (status, expires_at)
);

-- 用户行为分析表
CREATE TABLE user_behaviors (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    session_id VARCHAR(100),
    action_type VARCHAR(50) NOT NULL, -- page_view, button_click, query, etc.
    action_target VARCHAR(200),
    context_data JSONB,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_session (user_id, session_id),
    INDEX idx_action_timestamp (action_type, timestamp)
);

-- AI模型性能监控表
CREATE TABLE ai_model_metrics (
    id BIGSERIAL PRIMARY KEY,
    model_name VARCHAR(100) NOT NULL,
    model_version VARCHAR(20) NOT NULL,
    metric_type VARCHAR(50) NOT NULL, -- accuracy, precision, recall, latency
    metric_value DECIMAL(10,4),
    sample_count INTEGER,
    measured_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_model_version (model_name, model_version),
    INDEX idx_metric_measured (metric_type, measured_at)
);
```

### **4. 柔性业务规则数据模型**

```sql
-- 业务规则表
CREATE TABLE business_rules (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL UNIQUE,
    description TEXT,
    category VARCHAR(100),
    rule_type VARCHAR(50), -- validation, calculation, workflow, notification
    conditions JSONB, -- 规则触发条件
    actions JSONB,    -- 规则执行动作
    priority INTEGER DEFAULT 0,
    status VARCHAR(20) DEFAULT 'active',
    effective_from TIMESTAMP,
    effective_to TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 规则执行日志表
CREATE TABLE rule_execution_logs (
    id BIGSERIAL PRIMARY KEY,
    rule_id BIGINT NOT NULL REFERENCES business_rules(id),
    trigger_entity VARCHAR(100), -- 触发规则的实体类型
    trigger_id BIGINT,          -- 触发规则的实体ID
    execution_result VARCHAR(20), -- success, failed, skipped
    execution_details JSONB,
    executed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    execution_time INTEGER, -- 执行耗时(毫秒)
    INDEX idx_rule_executed (rule_id, executed_at),
    INDEX idx_entity_trigger (trigger_entity, trigger_id)
);
```

---

## 📅 **实施计划**

### **第一阶段：基础设施建设 (1-3个月)**

#### **1.1 NL2SQL中间件集成**
- **目标**: 实现基础的自然语言转SQL功能
- **任务**:
  - 集成Chat2DB SDK
  - 实现查询接口
  - 添加查询历史记录
  - 开发基础的前端组件
- **验收标准**:
  - 支持基本的CRUD查询
  - 查询响应时间 < 3秒
  - 前端组件正常工作

#### **1.2 AI集成服务搭建**
- **目标**: 建立AI能力的基础架构
- **任务**:
  - 部署AI推理服务
  - 实现模型管理接口
  - 建立数据管道
  - 配置GPU资源池
- **验收标准**:
  - AI服务正常运行
  - 模型推理响应时间 < 5秒
  - 支持模型热更新

#### **1.3 动态配置系统**
- **目标**: 实现配置的动态管理
- **任务**:
  - 开发配置管理界面
  - 实现配置版本控制
  - 添加配置生效监控
  - 集成配置热更新机制
- **验收标准**:
  - 支持配置的在线编辑
  - 配置变更生效时间 < 30秒
  - 完整的版本管理功能

### **第二阶段：核心功能实现 (4-8个月)**

#### **2.1 智能质量管理系统**
- **目标**: AI辅助的质量管理流程
- **任务**:
  - 实现偏差AI分析
  - 开发CAPA智能建议
  - 添加质量预测预警
  - 优化审核流程
- **验收标准**:
  - AI分析准确率 > 85%
  - 偏差处理时间减少30%
  - 用户满意度提升20%

#### **2.2 智能前端界面**
- **目标**: 提供智能化的用户体验
- **任务**:
  - 重构前端技术栈
  - 实现AI推荐面板
  - 开发自然语言查询界面
  - 添加智能导航和提示
- **验收标准**:
  - 前端页面加载时间 < 3秒
  - 用户操作效率提升40%
  - AI推荐采纳率 > 30%

#### **2.3 柔性业务规则引擎**
- **目标**: 实现业务逻辑的动态配置
- **任务**:
  - 开发规则引擎
  - 实现规则配置界面
  - 添加规则执行监控
  - 集成业务流程
- **验收标准**:
  - 支持规则的在线配置
  - 规则执行准确率100%
  - 配置变更响应时间 < 10秒

### **第三阶段：高级功能与优化 (9-12个月)**

#### **3.1 高级AI能力**
- **目标**: 实现深度学习和预测分析
- **任务**:
  - 集成深度学习模型
  - 实现预测性维护
  - 开发异常检测算法
  - 添加自然语言处理能力
- **验收标准**:
  - 预测准确率 > 90%
  - 异常检测召回率 > 95%
  - NLP理解准确率 > 85%

#### **3.2 系统优化与监控**
- **目标**: 提升系统性能和稳定性
- **任务**:
  - 性能优化和缓存策略
  - 完善监控和告警
  - 实现自动扩缩容
  - 添加安全加固
- **验收标准**:
  - 系统响应时间 < 2秒
  - 可用性 > 99.9%
  - 安全漏洞为0

#### **3.3 用户培训与文档**
- **目标**: 确保顺利上线和使用
- **任务**:
  - 制定培训计划
  - 编写用户文档
  - 实施上线支持
  - 收集用户反馈
- **验收标准**:
  - 用户培训覆盖率100%
  - 文档完整性 > 95%
  - 用户满意度 > 90%

---

## ⚠️ **风险评估与应对策略**

### **技术风险**

| 风险项目 | 概率 | 影响程度 | 应对策略 |
|---------|------|----------|----------|
| NL2SQL准确率不足 | 中 | 高 | 建立人工审核机制，持续优化算法 |
| AI模型性能不稳定 | 中 | 高 | 实现模型A/B测试，多模型融合 |
| 配置热更新失败 | 低 | 中 | 添加配置回滚机制，完善测试流程 |
| 前后端集成复杂 | 中 | 中 | 制定详细的API规范，实施敏捷开发 |

### **业务风险**

| 风险项目 | 概率 | 影响程度 | 应对策略 |
|---------|------|----------|----------|
| 用户接受度低 | 中 | 高 | 开展充分的用户调研，分阶段推广 |
| 业务流程变更 | 高 | 中 | 深入分析现有流程，渐进式优化 |
| 数据质量问题 | 中 | 高 | 建立数据治理体系，实施质量监控 |
| 合规性问题 | 低 | 高 | 咨询法规专家，确保符合GMP要求 |

### **项目管理风险**

| 风险项目 | 概率 | 影响程度 | 应对策略 |
|---------|------|----------|----------|
| 进度延误 | 中 | 中 | 制定详细的项目计划，设立里程碑 |
| 资源不足 | 低 | 高 | 提前规划资源需求，建立备用方案 |
| 需求变更频繁 | 高 | 中 | 实施需求变更控制流程，加强沟通 |
| 团队协作问题 | 低 | 中 | 建立明确的沟通机制，定期评审 |

---

## 📊 **预期收益评估**

### **直接经济效益**
1. **开发效率提升**: NL2SQL减少60%查询开发时间
2. **维护成本降低**: 动态配置减少40%运维工作
3. **质量事故减少**: AI预测降低30%质量问题
4. **用户效率提升**: 智能界面提高40%操作效率

### **间接价值提升**
1. **技术创新能力**: 建立AI应用的技术基础
2. **业务灵活性**: 支持快速响应业务变化
3. **用户体验改善**: 智能化界面提升满意度
4. **竞争优势建立**: 在行业中形成技术领先

### **量化指标**
- **ROI预期**: 第一年投资回收率150%
- **用户效率**: 整体效率提升35-45%
- **质量提升**: 质量事故减少25-35%
- **成本节约**: 运维成本降低30-40%

---

## 🎯 **总结与建议**

本次重构将GMP信息管理系统从传统架构转型为智能化、柔性化的现代化平台。通过引入NL2SQL中间件和全面AI集成，实现系统功能和用户体验的质的飞跃。

**关键成功要素**:
1. **技术选型正确**: 选择成熟稳定的技术和框架
2. **分阶段实施**: 降低风险，确保每个阶段都能产生价值
3. **用户中心设计**: 充分考虑用户需求，提升接受度
4. **持续优化**: 基于数据和反馈持续改进系统

**实施建议**:
- 成立跨部门的重构领导小组
- 建立完善的项目管理和质量控制流程
- 重视用户培训和变革管理
- 制定详细的回滚和应急预案

通过本次重构，GMP系统将具备更强的智能化能力和业务适应性，为医药企业的数字化转型提供强大支撑。

---

**报告编制**: GMP系统架构团队  
**审核日期**: 2025年12月16日  
**有效期**: 至重构项目完成
