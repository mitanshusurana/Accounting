import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity } from 'react-native';

import { AuthContext } from '../context/AuthContext';
import { useContext } from 'react';

export default function GatewayScreen({ navigation }) {
  const { logout } = useContext(AuthContext);
  return (
    <View style={styles.container}>
      <Text style={styles.header}>Gateway of ERP</Text>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Masters</Text>
        <TouchableOpacity style={styles.button} onPress={() => navigation.navigate('AccountForm')}>
          <Text style={styles.buttonText}>Account Info (Ledger Creation)</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.button} onPress={() => navigation.navigate('Product')}>
          <Text style={styles.buttonText}>Inventory Info (Stock Items)</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.button} onPress={() => navigation.navigate('Godown')}>
          <Text style={styles.buttonText}>Godown (Warehouse) Creation</Text>
        </TouchableOpacity>
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Transactions</Text>
        <TouchableOpacity style={styles.button} onPress={() => navigation.navigate('JournalEntry')}>
          <Text style={styles.buttonText}>Accounting Vouchers</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.button} onPress={() => navigation.navigate('StockMovement')}>
          <Text style={styles.buttonText}>Inventory Vouchers</Text>
        </TouchableOpacity>
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Reports</Text>
        <TouchableOpacity style={styles.button} onPress={() => navigation.navigate('BalanceSheet')}>
          <Text style={styles.buttonText}>Balance Sheet</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.button} onPress={() => navigation.navigate('ProfitLoss')}>
          <Text style={styles.buttonText}>Profit & Loss A/c</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.button} onPress={() => navigation.navigate('TrialBalance')}>
          <Text style={styles.buttonText}>Trial Balance</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.button} onPress={() => navigation.navigate('Daybook')}>
          <Text style={styles.buttonText}>Day Book</Text>
        </TouchableOpacity>
      </View>

      <View style={styles.section}>
        <TouchableOpacity style={styles.button} onPress={logout}>
          <Text style={styles.buttonText}>Logout</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 20,
    backgroundColor: '#fff',
  },
  header: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 30,
    textAlign: 'center',
  },
  section: {
    marginBottom: 20,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 10,
    color: '#333',
  },
  button: {
    backgroundColor: '#f0f0f0',
    padding: 15,
    borderRadius: 5,
    marginBottom: 10,
    borderWidth: 1,
    borderColor: '#ddd',
  },
  buttonText: {
    fontSize: 16,
    color: '#007bff',
  }
});
