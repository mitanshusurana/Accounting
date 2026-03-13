import React, { useState, useContext } from 'react';
import { View, Text, TextInput, Button, StyleSheet, ScrollView, Alert, Platform } from 'react-native';
import { AuthContext } from '../context/AuthContext';

const API_URL = process.env.EXPO_PUBLIC_API_URL || (Platform.OS === 'android' ? 'http://10.0.2.2:8080/api' : 'http://localhost:8080/api');

export default function InvoiceScreen() {
  const { userToken } = useContext(AuthContext);
  const [invoiceType, setInvoiceType] = useState('Sales'); // Sales or Purchase
  const [partyAccountId, setPartyAccountId] = useState('');
  const [transactionDate, setTransactionDate] = useState('');
  const [narration, setNarration] = useState('');

  const [items, setItems] = useState([
    { productId: '', quantity: '0', rate: '0', godownId: '' }
  ]);

  const updateItem = (index, field, value) => {
    const newItems = [...items];
    newItems[index][field] = value;
    setItems(newItems);
  };

  const addItemLine = () => {
    setItems([...items, { productId: '', quantity: '0', rate: '0', godownId: '' }]);
  };

  const handleSubmit = async () => {
    try {
      const payload = {
        invoiceType,
        partyAccountId,
        transactionDate,
        narration,
        items: items.map(item => ({
            ...item,
            quantity: parseFloat(item.quantity),
            rate: parseFloat(item.rate)
        }))
      };

      const response = await fetch(`${API_URL}/inventory/invoice`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${userToken}`
        },
        body: JSON.stringify(payload),
      });

      if (response.ok) {
        Alert.alert("Success", "Invoice saved successfully!");
        setItems([{ productId: '', quantity: '0', rate: '0', godownId: '' }]);
      } else {
        const err = await response.text();
        Alert.alert("Error", `Failed to save invoice: ${err}`);
      }

    } catch (error) {
      Alert.alert("Error", `Network error: ${error.message}`);
    }
  };

  return (
    <ScrollView style={styles.container}>
      <Text style={styles.header}>Sales/Purchase Invoice</Text>

      <View style={styles.headerForm}>
        <TextInput style={styles.input} placeholder="Voucher Type (Sales/Purchase)" value={invoiceType} onChangeText={setInvoiceType} />
        <TextInput style={styles.input} placeholder="Party A/c Name (e.g. Assets.Debtors.John)" value={partyAccountId} onChangeText={setPartyAccountId} />
        <TextInput style={styles.input} placeholder="Date (YYYY-MM-DD)" value={transactionDate} onChangeText={setTransactionDate} />
        <TextInput style={styles.input} placeholder="Narration" value={narration} onChangeText={setNarration} />
      </View>

      <Text style={styles.subHeader}>Stock Items</Text>
      {items.map((item, index) => (
        <View key={index} style={styles.itemContainer}>
          <Text style={styles.itemLabel}>Item {index + 1}</Text>
          <TextInput style={styles.input} placeholder="Product ID" value={item.productId} onChangeText={(val) => updateItem(index, 'productId', val)} />
          <TextInput style={styles.input} placeholder="Godown ID" value={item.godownId} onChangeText={(val) => updateItem(index, 'godownId', val)} />
          <View style={styles.row}>
            <TextInput style={[styles.input, styles.halfInput]} placeholder="Quantity" keyboardType="numeric" value={item.quantity} onChangeText={(val) => updateItem(index, 'quantity', val)} />
            <TextInput style={[styles.input, styles.halfInput]} placeholder="Rate" keyboardType="numeric" value={item.rate} onChangeText={(val) => updateItem(index, 'rate', val)} />
          </View>
        </View>
      ))}

      <View style={styles.buttonRow}>
        <Button title="Add Item" onPress={addItemLine} color="#666" />
        <Button title="Save Invoice" onPress={handleSubmit} color="#28a745" />
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 20, backgroundColor: '#fff' },
  header: { fontSize: 24, fontWeight: 'bold', marginBottom: 20, textAlign: 'center' },
  subHeader: { fontSize: 18, fontWeight: '600', marginTop: 20, marginBottom: 10 },
  headerForm: { marginBottom: 10 },
  input: { borderWidth: 1, borderColor: '#ccc', padding: 10, marginBottom: 10, borderRadius: 5 },
  itemContainer: { borderWidth: 1, borderColor: '#eee', padding: 10, marginBottom: 15, borderRadius: 5, backgroundColor: '#f9f9f9' },
  itemLabel: { fontWeight: 'bold', marginBottom: 5 },
  row: { flexDirection: 'row', justifyContent: 'space-between' },
  halfInput: { width: '48%' },
  buttonRow: { flexDirection: 'row', justifyContent: 'space-around', marginTop: 20, marginBottom: 40 }
});
