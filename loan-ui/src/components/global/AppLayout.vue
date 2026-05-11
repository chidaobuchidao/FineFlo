<template>
  <div class="app-layout">
    <SidebarNav />
    <TopBar :title="pageTitle" />
    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="page" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import SidebarNav from './SidebarNav.vue'
import TopBar from './TopBar.vue'

const route = useRoute()

const titleMap: Record<string, string> = {
  Dashboard: '仪表盘',
  EnterpriseList: '企业管理',
  EnterpriseDetail: '企业详情',
  ApprovalList: '贷款审批',
  ApprovalDetail: '审批详情',
  DisbursementList: '放款管理',
  RepaymentList: '还款管理',
  OverdueList: '逾期管理',
  Statistics: '数据统计',
}

const pageTitle = computed(() => titleMap[route.name as string] || '普惠金融')
</script>

<style scoped>
.app-layout { height: 100%; }
.main-content {
  margin-left: var(--sidebar-width);
  margin-top: var(--topbar-height);
  padding: var(--space-8);
  min-height: calc(100vh - var(--topbar-height));
}
</style>
