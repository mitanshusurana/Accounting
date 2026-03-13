import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import GatewayScreen from './screens/GatewayScreen';
import JournalEntryForm from './screens/JournalEntryForm';
import AccountFormScreen from './screens/AccountFormScreen';
import DaybookScreen from './screens/DaybookScreen';
import TrialBalanceScreen from './screens/TrialBalanceScreen';
import ProfitLossScreen from './screens/ProfitLossScreen';
import BalanceSheetScreen from './screens/BalanceSheetScreen';

const Stack = createNativeStackNavigator();

export default function App() {
  return (
    <NavigationContainer>
      <Stack.Navigator initialRouteName="Gateway">
        <Stack.Screen name="Gateway" component={GatewayScreen} options={{ title: 'Gateway of ERP' }} />
        <Stack.Screen name="AccountForm" component={AccountFormScreen} options={{ title: 'Ledger Creation' }} />
        <Stack.Screen name="JournalEntry" component={JournalEntryForm} options={{ title: 'Accounting Voucher' }} />
        <Stack.Screen name="Daybook" component={DaybookScreen} options={{ title: 'Daybook' }} />
        <Stack.Screen name="TrialBalance" component={TrialBalanceScreen} options={{ title: 'Trial Balance' }} />
        <Stack.Screen name="ProfitLoss" component={ProfitLossScreen} options={{ title: 'Profit & Loss A/c' }} />
        <Stack.Screen name="BalanceSheet" component={BalanceSheetScreen} options={{ title: 'Balance Sheet' }} />
      </Stack.Navigator>
    </NavigationContainer>
  );
}
