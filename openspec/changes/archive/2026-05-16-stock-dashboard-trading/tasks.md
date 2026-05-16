## 1. Backend - Simulated Quote API

- [x] 1.1 Add `MarketController.simulatedQuotes()` endpoint (`POST /market/simulated-quotes`) accepting list of stock codes and returning batch simulated quotes
- [x] 1.2 Implement `MarketService.getSimulatedQuotes()` with random fluctuation logic (±0.3% ~ ±1.5%) based on real price from DB/cache, with random-walk fallback
- [x] 1.3 Add unit tests for simulated quote generation (real data base, fallback, edge cases)

## 2. Frontend - Stock Selector Component

- [x] 2.1 Create `StockSelector.vue` component with dropdown list (fuzzy filterable) and hot-stock quick-select buttons
- [x] 2.2 Replace `StockSearch.vue` usage in `OrderForm.vue` with `StockSelector.vue`, wire up auto-fill of stock code/name/price on selection
- [x] 2.3 Add `fetchAllStocks()` API function to `api.ts` for populating dropdown

## 3. Frontend - Real-time Dashboard

- [x] 3.1 Add `getSimulatedQuotes()` API function to `api.ts`
- [x] 3.2 Create dashboard stock card grid in `DashboardView.vue` with 6-8 popular stocks, 3-second auto-refresh polling, and CSS transition animations on price change
- [x] 3.3 Add stock add/remove controls to dashboard (dropdown to add, close button to remove)
- [x] 3.4 Add "模拟数据" (simulated data) indicator label to dashboard

## 4. Frontend - Color Convention Flip (红涨绿跌)

- [x] 4.1 Flip price change colors in `DashboardView.vue` (overview P&L, quote section, position table)
- [x] 4.2 Flip buy/sell button colors in `OrderForm.vue` (buy = red, sell = green) — already Chinese convention
- [x] 4.3 Flip direction colors in `OrderList.vue` (buy orders = red, sell orders = green) — already Chinese convention
- [x] 4.4 Flip price change colors in `MarketView.vue` and `KLineChart.vue` (candlestick up = red, down = green) — MarketView flipped, KLineChart already Chinese convention
- [x] 4.5 Flip P&L colors in `PortfolioView.vue` — already Chinese convention (red-up/green-down)

## 5. Integration & Verification

- [x] 5.1 End-to-end manual test: dashboard auto-refresh, stock selection, order placement with new flow — verified via browser screenshots
- [x] 5.2 Verify all price displays follow red-up/green-down convention across all pages — verified on Dashboard, Trading, Market pages
- [x] 5.3 Verify build passes (`npm run build` in frontend, `mvn package` in backend) — both pass
