import React, { useState, useContext } from 'react';
import { View, Text, TextInput, Button, StyleSheet, Alert, Platform } from 'react-native';
import { AuthContext } from '../context/AuthContext';

const API_URL = process.env.EXPO_PUBLIC_API_URL || (Platform.OS === 'android' ? 'http://10.0.2.2:8080/api' : 'http://localhost:8080/api');

export default function AccountFormScreen() {
  const { userToken } = useContext(AuthContext);
  const [accountId, setAccountId] = useState('');
  const [name, setName] = useState('');
  const [accountType, setAccountType] = useState('ASSET');
  const [ltreePath, setLtreePath] = useState('');

  const handleSubmit = async () => {
    try {
      const payload = {
        accountId,
        name,
        accountType,
        ltreePath
      };

      const response = await fetch(`${API_URL}/accounts`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${userToken}`
        },
        body: JSON.stringify(payload),
      });

      if (response.ok) {
        Alert.alert("Success", "Account/Ledger created successfully!");
        setAccountId('');
        setName('');
        setAccountType('ASSET');
        setLtreePath('');
      } else {
        const errData = await response.json();
        Alert.alert("Error", `Failed to create account: ${errData.message || response.status}`);
      }
    } catch (error) {
      Alert.alert("Error", `Network error: ${error.message}`);
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.header}>Create New Ledger</Text>

      <TextInput
        style={styles.input}
        placeholder="Account ID (e.g. Assets.Bank.SBI)"
        value={accountId}
        onChangeText={setAccountId}
      />

      <TextInput
        style={styles.input}
        placeholder="Ledger Name (e.g. State Bank of India)"
        value={name}
        onChangeText={setName}
      />

      <TextInput
        style={styles.input}
        placeholder="Account Type (ASSET, LIABILITY, EQUITY, EXPENSE, REVENUE)"
        value={accountType}
        onChangeText={setAccountType}
      />

      <TextInput
        style={styles.input}
        placeholder="Ltree Path (e.g. Assets.Bank.SBI)"
        value={ltreePath}
        onChangeText={setLtreePath}
      />

      <Button title="Save Ledger" onPress={handleSubmit} color="#007bff" />
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
    fontSize: 22,
    fontWeight: 'bold',
    marginBottom: 20,
    textAlign: 'center',
  },
  input: {
    borderWidth: 1,
    borderColor: '#ccc',
    padding: 10,
    marginBottom: 15,
    borderRadius: 5,
  }
});
