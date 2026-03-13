import React, { useState, useEffect, useContext } from 'react';
import { AuthContext } from '../context/AuthContext';
import { View, Text, StyleSheet, FlatList, ActivityIndicator, Platform } from 'react-native';

const API_URL = process.env.EXPO_PUBLIC_API_URL || (Platform.OS === 'android' ? 'http://10.0.2.2:8080/api' : 'http://localhost:8080/api');

export default function DaybookScreen() {
  const { userToken } = useContext(AuthContext);
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchDaybook();
  }, []);

  const fetchDaybook = async () => {
    try {
      const today = new Date().toISOString().split('T')[0];
      const firstDay = new Date(new Date().getFullYear(), new Date().getMonth(), 1).toISOString().split('T')[0];

      const response = await fetch(`${API_URL}/reports/daybook?startDate=${firstDay}&endDate=${today}`, { headers: { 'Authorization': `Bearer ${userToken}` } });
      const json = await response.json();
      setData(json);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const renderItem = ({ item }) => (
    <View style={styles.card}>
      <View style={styles.cardHeader}>
        <Text style={styles.date}>{item.transactionDate}</Text>
        <Text style={styles.voucherType}>{item.voucherType}</Text>
      </View>
      <Text style={styles.narration}>{item.narration}</Text>

      {item.postings && item.postings.map((p, i) => (
        <View key={i} style={styles.postingRow}>
          <Text style={styles.account}>{p.accountId}</Text>
          <Text style={styles.amount}>
            {p.debitAmount > 0 ? `Dr ${p.debitAmount}` : `Cr ${p.creditAmount}`}
          </Text>
        </View>
      ))}
    </View>
  );

  return (
    <View style={styles.container}>
      <Text style={styles.header}>Day Book</Text>

      {loading ? (
        <ActivityIndicator size="large" color="#0000ff" />
      ) : (
        <FlatList
          data={data}
          keyExtractor={(item) => item.entryId}
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
    backgroundColor: '#f5f5f5',
  },
  header: {
    fontSize: 20,
    fontWeight: 'bold',
    textAlign: 'center',
    marginBottom: 15,
  },
  card: {
    backgroundColor: '#fff',
    padding: 15,
    marginBottom: 10,
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#ddd',
  },
  cardHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 10,
  },
  date: {
    fontWeight: 'bold',
  },
  voucherType: {
    color: '#666',
    fontStyle: 'italic',
  },
  narration: {
    marginBottom: 10,
    color: '#444',
  },
  postingRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    paddingVertical: 5,
    borderTopWidth: 1,
    borderTopColor: '#f0f0f0',
  },
  account: {
    flex: 2,
    fontSize: 13,
  },
  amount: {
    flex: 1,
    textAlign: 'right',
    fontSize: 13,
    fontWeight: '500',
  }
});
