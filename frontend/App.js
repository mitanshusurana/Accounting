import React, { useContext } from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import GatewayScreen from './screens/GatewayScreen';
import JournalEntryForm from './screens/JournalEntryForm';
import AccountFormScreen from './screens/AccountFormScreen';
import DaybookScreen from './screens/DaybookScreen';
import TrialBalanceScreen from './screens/TrialBalanceScreen';
import ProfitLossScreen from './screens/ProfitLossScreen';
import BalanceSheetScreen from './screens/BalanceSheetScreen';
import LoginScreen from './screens/LoginScreen';
import RegisterScreen from './screens/RegisterScreen';
import ProductScreen from './screens/ProductScreen';
import GodownScreen from './screens/GodownScreen';
import StockMovementScreen from './screens/StockMovementScreen';
import InvoiceScreen from './screens/InvoiceScreen';
import BankReconciliationScreen from './screens/BankReconciliationScreen';
import GstReturnsScreen from './screens/GstReturnsScreen';
import { AuthProvider, AuthContext } from './context/AuthContext';

const Stack = createNativeStackNavigator();

function Navigation() {
  const { userToken } = useContext(AuthContext);

  return (
    <NavigationContainer>
      <Stack.Navigator>
        {userToken == null ? (
          <>
            <Stack.Screen name="Login" component={LoginScreen} options={{ title: 'Login' }} />
            <Stack.Screen name="Register" component={RegisterScreen} options={{ title: 'Register' }} />
          </>
        ) : (
          <>
            <Stack.Screen name="Gateway" component={GatewayScreen} options={{ title: 'Gateway of ERP' }} />
            <Stack.Screen name="AccountForm" component={AccountFormScreen} options={{ title: 'Ledger Creation' }} />
            <Stack.Screen name="JournalEntry" component={JournalEntryForm} options={{ title: 'Accounting Voucher' }} />
            <Stack.Screen name="Daybook" component={DaybookScreen} options={{ title: 'Daybook' }} />
            <Stack.Screen name="TrialBalance" component={TrialBalanceScreen} options={{ title: 'Trial Balance' }} />
            <Stack.Screen name="ProfitLoss" component={ProfitLossScreen} options={{ title: 'Profit & Loss A/c' }} />
            <Stack.Screen name="BalanceSheet" component={BalanceSheetScreen} options={{ title: 'Balance Sheet' }} />
            <Stack.Screen name="Product" component={ProductScreen} options={{ title: 'Product Creation' }} />
            <Stack.Screen name="Godown" component={GodownScreen} options={{ title: 'Godown Creation' }} />
            <Stack.Screen name="StockMovement" component={StockMovementScreen} options={{ title: 'Stock Movement' }} />
            <Stack.Screen name="Invoice" component={InvoiceScreen} options={{ title: 'Invoice' }} />
            <Stack.Screen name="BankReconciliation" component={BankReconciliationScreen} options={{ title: 'Bank Reconciliation' }} />
            <Stack.Screen name="GstReturns" component={GstReturnsScreen} options={{ title: 'GST Returns' }} />
          </>
        )}
      </Stack.Navigator>
    </NavigationContainer>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <Navigation />
    </AuthProvider>
  );
}
