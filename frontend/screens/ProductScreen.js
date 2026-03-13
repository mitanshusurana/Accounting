import React, { useState, useContext } from 'react';
import { View, Text, TextInput, Button, StyleSheet, Alert, Platform } from 'react-native';
import { AuthContext } from '../context/AuthContext';

const API_URL = process.env.EXPO_PUBLIC_API_URL || (Platform.OS === 'android' ? 'http://10.0.2.2:8080/api' : 'http://localhost:8080/api');

export default function ProductScreen() {
  const { userToken } = useContext(AuthContext);
  const [productId, setProductId] = useState('');
  const [sku, setSku] = useState('');
  const [name, setName] = useState('');
  const [hsnCode, setHsnCode] = useState('');
  const [defaultPrice, setDefaultPrice] = useState('0');

  const handleSubmit = async () => {
    try {
      const payload = {
        productId,
        sku,
        name,
        hsnCode,
        defaultPrice: parseFloat(defaultPrice)
      };

      const response = await fetch(`${API_URL}/inventory/products`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${userToken}`
        },
        body: JSON.stringify(payload),
      });

      if (response.ok) {
        Alert.alert("Success", "Product created successfully!");
        setProductId('');
        setSku('');
        setName('');
        setHsnCode('');
        setDefaultPrice('0');
      } else {
        Alert.alert("Error", `Failed to create product.`);
      }
    } catch (error) {
      Alert.alert("Error", `Network error: ${error.message}`);
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.header}>Create Stock Item (Product)</Text>

      <TextInput
        style={styles.input}
        placeholder="Product ID"
        value={productId}
        onChangeText={setProductId}
      />

      <TextInput
        style={styles.input}
        placeholder="SKU"
        value={sku}
        onChangeText={setSku}
      />

      <TextInput
        style={styles.input}
        placeholder="Product Name"
        value={name}
        onChangeText={setName}
      />

      <TextInput
        style={styles.input}
        placeholder="HSN Code"
        value={hsnCode}
        onChangeText={setHsnCode}
      />

      <TextInput
        style={styles.input}
        placeholder="Default Price"
        keyboardType="numeric"
        value={defaultPrice}
        onChangeText={setDefaultPrice}
      />

      <Button title="Save Product" onPress={handleSubmit} color="#007bff" />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 20, backgroundColor: '#fff' },
  header: { fontSize: 22, fontWeight: 'bold', marginBottom: 20, textAlign: 'center' },
  input: { borderWidth: 1, borderColor: '#ccc', padding: 10, marginBottom: 15, borderRadius: 5 }
});
