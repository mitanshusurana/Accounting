import React, { useState, useEffect, useContext } from 'react';
import { AuthContext } from '../context/AuthContext';
import { View, Text, StyleSheet, FlatList, ActivityIndicator, Platform } from 'react-native';

const API_URL = process.env.EXPO_PUBLIC_API_URL || (Platform.OS === 'android' ? 'http://10.0.2.2:8080/api' : 'http://localhost:8080/api');

export default function BalanceSheetScreen() {
  const { userToken } = useContext(AuthContext);
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchBalanceSheet();
  }, []);

  const fetchBalanceSheet = async () => {
    try {
      const today = new Date().toISOString().split('T')[0];
      const response = await fetch(`${API_URL}/reports/trial-balance?asOfDate=${today}`, { headers: { 'Authorization': `Bearer ${userToken}` } });
      const json = await response.json();

      const bsData = json.filter(item =>
        item.accountType === 'ASSET' || item.accountType === 'LIABILITY' || item.accountType === 'EQUITY'
      );

      setData(bsData);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const renderItem = ({ item }) => (
    <View style={styles.row}>
      <Text style={styles.colName}>{item.accountName}</Text>
      <Text style={styles.colAmount}>{item.accountType === 'ASSET' ? item.debitBalance : item.creditBalance}</Text>
    </View>
  );

  return (
    <View style={styles.container}>
      <Text style={styles.header}>Balance Sheet</Text>

      <View style={[styles.row, styles.headerRow]}>
        <Text style={[styles.colName, styles.bold]}>Particulars</Text>
        <Text style={[styles.colAmount, styles.bold]}>Amount</Text>
      </View>

      {loading ? (
        <ActivityIndicator size="large" color="#0000ff" />
      ) : (
        <FlatList
          data={data}
          keyExtractor={(item) => item.accountId}
          renderItem={renderItem}
        />
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 10,
    backgroundColor: '#fff',
  },
  header: {
    fontSize: 20,
    fontWeight: 'bold',
    textAlign: 'center',
    marginBottom: 15,
  },
  row: {
    flexDirection: 'row',
    paddingVertical: 10,
    borderBottomWidth: 1,
    borderBottomColor: '#eee',
  },
  headerRow: {
    backgroundColor: '#f8f8f8',
    borderTopWidth: 1,
    borderTopColor: '#ccc',
    borderBottomWidth: 2,
    borderBottomColor: '#ccc',
  },
  colName: {
    flex: 2,
    fontSize: 14,
  },
  colAmount: {
    flex: 1,
    textAlign: 'right',
    fontSize: 14,
  },
  bold: {
    fontWeight: 'bold',
  }
});
