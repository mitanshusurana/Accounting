import React, { useState, useContext } from 'react';
import { View, Text, TextInput, Button, StyleSheet, Alert, Platform, ScrollView } from 'react-native';
import { AuthContext } from '../context/AuthContext';

const API_URL = process.env.EXPO_PUBLIC_API_URL || (Platform.OS === 'android' ? 'http://10.0.2.2:8080/api' : 'http://localhost:8080/api');

export default function StockMovementScreen() {
  const { userToken } = useContext(AuthContext);
  const [productId, setProductId] = useState('');
  const [godownId, setGodownId] = useState('');
  const [batchId, setBatchId] = useState('');
  const [quantity, setQuantity] = useState('0');
  const [direction, setDirection] = useState('IN'); // IN or OUT
  const [unitCost, setUnitCost] = useState('0');
  const [movementDate, setMovementDate] = useState('');

  const handleSubmit = async () => {
    try {
      const payload = {
        productId,
        godownId,
        batchId,
        quantity: parseFloat(quantity),
        direction,
        unitCost: parseFloat(unitCost),
        movementDate
      };

      const response = await fetch(`${API_URL}/inventory/stock-movements`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${userToken}`
        },
        body: JSON.stringify(payload),
      });

      if (response.ok) {
        Alert.alert("Success", "Stock Movement recorded!");
        setBatchId('');
        setQuantity('0');
      } else {
        Alert.alert("Error", `Failed to record stock movement.`);
      }
    } catch (error) {
      Alert.alert("Error", `Network error: ${error.message}`);
    }
  };

  return (
    <ScrollView style={styles.container}>
      <Text style={styles.header}>Record Stock Movement</Text>

      <TextInput
        style={styles.input}
        placeholder="Movement Date (YYYY-MM-DD)"
        value={movementDate}
        onChangeText={setMovementDate}
      />
      <TextInput
        style={styles.input}
        placeholder="Product ID"
        value={productId}
        onChangeText={setProductId}
      />
      <TextInput
        style={styles.input}
        placeholder="Godown ID"
        value={godownId}
        onChangeText={setGodownId}
      />
      <TextInput
        style={styles.input}
        placeholder="Batch ID"
        value={batchId}
        onChangeText={setBatchId}
      />
      <TextInput
        style={styles.input}
        placeholder="Direction (IN or OUT)"
        value={direction}
        onChangeText={setDirection}
      />
      <TextInput
        style={styles.input}
        placeholder="Quantity"
        keyboardType="numeric"
        value={quantity}
        onChangeText={setQuantity}
      />
      <TextInput
        style={styles.input}
        placeholder="Unit Cost"
        keyboardType="numeric"
        value={unitCost}
        onChangeText={setUnitCost}
      />

      <Button title="Save Movement" onPress={handleSubmit} color="#007bff" />
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 20, backgroundColor: '#fff' },
  header: { fontSize: 22, fontWeight: 'bold', marginBottom: 20, textAlign: 'center' },
  input: { borderWidth: 1, borderColor: '#ccc', padding: 10, marginBottom: 15, borderRadius: 5 }
});
