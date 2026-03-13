import React, { useState, useEffect, useContext } from 'react';
import { AuthContext } from '../context/AuthContext';
import { View, Text, StyleSheet, FlatList, ActivityIndicator, Platform } from 'react-native';

const API_URL = Platform.OS === 'android' ? 'http://10.0.2.2:8080/api' : 'http://localhost:8080/api';

export default function ProfitLossScreen() {
  const { userToken } = useContext(AuthContext);
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [netProfit, setNetProfit] = useState(0);

  useEffect(() => {
    fetchProfitLoss();
  }, []);

  const fetchProfitLoss = async () => {
    try {
      const today = new Date().toISOString().split('T')[0];
      const response = await fetch(`${API_URL}/reports/trial-balance?asOfDate=${today}`, { headers: { 'Authorization': `Bearer ${userToken}` } });
      const json = await response.json();

      const plData = json.filter(item =>
        item.accountType === 'EXPENSE' || item.accountType === 'REVENUE'
      );

      let profit = 0;
      plData.forEach(item => {
        if (item.accountType === 'REVENUE') {
            profit += item.creditBalance;
            profit -= item.debitBalance;
        } else if (item.accountType === 'EXPENSE') {
            profit -= item.debitBalance;
            profit += item.creditBalance;
        }
      });

      setNetProfit(profit);
      setData(plData);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const renderItem = ({ item }) => (
    <View style={styles.row}>
      <Text style={styles.colName}>{item.accountName}</Text>
      <Text style={styles.colAmount}>{item.accountType === 'EXPENSE' ? item.debitBalance : item.creditBalance}</Text>
    </View>
  );

  return (
    <View style={styles.container}>
      <Text style={styles.header}>Profit & Loss A/c</Text>

      <View style={[styles.row, styles.headerRow]}>
        <Text style={[styles.colName, styles.bold]}>Particulars</Text>
        <Text style={[styles.colAmount, styles.bold]}>Amount</Text>
      </View>

      {loading ? (
        <ActivityIndicator size="large" color="#0000ff" />
      ) : (
        <>
            <FlatList
            data={data}
            keyExtractor={(item) => item.accountId}
            renderItem={renderItem}
            />
            <View style={[styles.row, styles.totalRow]}>
                <Text style={[styles.colName, styles.bold]}>Net Profit / (Loss)</Text>
                <Text style={[styles.colAmount, styles.bold]}>{netProfit}</Text>
            </View>
        </>
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
  totalRow: {
    borderTopWidth: 2,
    borderTopColor: '#000',
    marginTop: 10,
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
