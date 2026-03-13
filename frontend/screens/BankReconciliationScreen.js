import React, { useState, useContext } from 'react';
import { View, Text, Button, StyleSheet, Alert, Platform, FlatList, ActivityIndicator } from 'react-native';
import * as DocumentPicker from 'expo-document-picker';
import { AuthContext } from '../context/AuthContext';

const API_URL = process.env.EXPO_PUBLIC_API_URL || (Platform.OS === 'android' ? 'http://10.0.2.2:8080/api' : 'http://localhost:8080/api');

export default function BankReconciliationScreen() {
  const { userToken } = useContext(AuthContext);
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(false);

  const handleUploadReal = async () => {
    try {
      const result = await DocumentPicker.getDocumentAsync({
        type: 'application/pdf',
      });

      if (result.canceled) return;

      setLoading(true);
      const file = result.assets[0];

      const formData = new FormData();
      formData.append('file', {
        uri: file.uri,
        name: file.name,
        type: file.mimeType || 'application/pdf'
      });

      const response = await fetch(`${API_URL}/banking/parse-statement`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${userToken}`,
        },
        body: formData
      });

      if (response.ok) {
        const data = await response.json();
        setTransactions(data);
        Alert.alert("Success", "Bank statement parsed successfully!");
      } else {
        const err = await response.text();
        Alert.alert("Error", `Failed to parse statement. ${err}`);
      }
    } catch (error) {
      Alert.alert("Error", `Network error: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const renderItem = ({ item }) => (
    <View style={styles.row}>
      <Text style={styles.colDate}>{item.transactionDate}</Text>
      <Text style={styles.colDesc}>{item.description}</Text>
      <Text style={styles.colAmount}>{item.debitAmount > 0 ? `Dr ${item.debitAmount}` : `Cr ${item.creditAmount}`}</Text>
    </View>
  );

  return (
    <View style={styles.container}>
      <Text style={styles.header}>Bank Reconciliation</Text>

      <Button title="Upload Bank Statement (PDF)" onPress={handleUploadReal} color="#007bff" />

      {loading ? (
        <ActivityIndicator size="large" color="#0000ff" style={{marginTop: 20}} />
      ) : (
        <View style={styles.listContainer}>
          <View style={[styles.row, styles.headerRow]}>
            <Text style={[styles.colDate, styles.bold]}>Date</Text>
            <Text style={[styles.colDesc, styles.bold]}>Description</Text>
            <Text style={[styles.colAmount, styles.bold]}>Amount</Text>
          </View>
          <FlatList
            data={transactions}
            keyExtractor={(item, index) => index.toString()}
            renderItem={renderItem}
          />
        </View>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 20, backgroundColor: '#fff' },
  header: { fontSize: 22, fontWeight: 'bold', marginBottom: 20, textAlign: 'center' },
  listContainer: { marginTop: 20, flex: 1 },
  row: { flexDirection: 'row', paddingVertical: 10, borderBottomWidth: 1, borderBottomColor: '#eee' },
  headerRow: { backgroundColor: '#f8f8f8', borderTopWidth: 1, borderTopColor: '#ccc', borderBottomWidth: 2, borderBottomColor: '#ccc' },
  colDate: { flex: 1, fontSize: 13 },
  colDesc: { flex: 2, fontSize: 13 },
  colAmount: { flex: 1, textAlign: 'right', fontSize: 13 },
  bold: { fontWeight: 'bold' }
});
