import React, { useState, useEffect } from 'react';
import { View, Text, TextInput, Button, StyleSheet, FlatList, Alert } from 'react-native';

const API_URL = process.env.EXPO_PUBLIC_API_URL || 'http://localhost:8080/api';

export default function PartyScreen() {
  const [parties, setParties] = useState([]);
  const [name, setName] = useState('');
  const [type, setType] = useState('CUSTOMER'); // CUSTOMER, SUPPLIER, BOTH
  const [gstin, setGstin] = useState('');

  useEffect(() => {
    fetchParties();
  }, []);

  const fetchParties = async () => {
    try {
      const response = await fetch(`${API_URL}/parties`);
      const data = await response.json();
      setParties(data);
    } catch (error) {
      console.error(error);
      Alert.alert('Error', 'Failed to fetch parties');
    }
  };

  const handleSave = async () => {
    try {
      const response = await fetch(`${API_URL}/parties`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, type, gstin })
      });
      if (response.ok) {
        Alert.alert('Success', 'Party saved successfully');
        fetchParties();
        setName('');
        setGstin('');
      } else {
        Alert.alert('Error', 'Failed to save party');
      }
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.header}>Manage Parties</Text>

      <View style={styles.form}>
        <TextInput
          style={styles.input}
          placeholder="Party Name"
          value={name}
          onChangeText={setName}
        />
        <TextInput
          style={styles.input}
          placeholder="Type (CUSTOMER/SUPPLIER/BOTH)"
          value={type}
          onChangeText={setType}
        />
        <TextInput
          style={styles.input}
          placeholder="GSTIN"
          value={gstin}
          onChangeText={setGstin}
        />
        <Button title="Save Party" onPress={handleSave} />
      </View>

      <FlatList
        data={parties}
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => (
          <View style={styles.item}>
            <Text style={styles.itemTitle}>{item.name} ({item.type})</Text>
            <Text>GSTIN: {item.gstin}</Text>
          </View>
        )}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 20, backgroundColor: '#f5f5f5' },
  header: { fontSize: 24, fontWeight: 'bold', marginBottom: 20 },
  form: { backgroundColor: '#fff', padding: 15, borderRadius: 8, marginBottom: 20 },
  input: { borderWidth: 1, borderColor: '#ccc', borderRadius: 4, padding: 10, marginBottom: 10 },
  item: { backgroundColor: '#fff', padding: 15, borderRadius: 8, marginBottom: 10 },
  itemTitle: { fontSize: 16, fontWeight: 'bold' }
});
