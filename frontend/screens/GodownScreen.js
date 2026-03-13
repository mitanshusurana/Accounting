import React, { useState, useContext } from 'react';
import { View, Text, TextInput, Button, StyleSheet, Alert, Platform } from 'react-native';
import { AuthContext } from '../context/AuthContext';

const API_URL = process.env.EXPO_PUBLIC_API_URL || (Platform.OS === 'android' ? 'http://10.0.2.2:8080/api' : 'http://localhost:8080/api');

export default function GodownScreen() {
  const { userToken } = useContext(AuthContext);
  const [godownId, setGodownId] = useState('');
  const [name, setName] = useState('');
  const [location, setLocation] = useState('');

  const handleSubmit = async () => {
    try {
      const payload = {
        godownId,
        name,
        location
      };

      const response = await fetch(`${API_URL}/inventory/godowns`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${userToken}`
        },
        body: JSON.stringify(payload),
      });

      if (response.ok) {
        Alert.alert("Success", "Godown created successfully!");
        setGodownId('');
        setName('');
        setLocation('');
      } else {
        Alert.alert("Error", `Failed to create godown.`);
      }
    } catch (error) {
      Alert.alert("Error", `Network error: ${error.message}`);
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.header}>Create Godown (Warehouse)</Text>

      <TextInput
        style={styles.input}
        placeholder="Godown ID (e.g. WH-1)"
        value={godownId}
        onChangeText={setGodownId}
      />

      <TextInput
        style={styles.input}
        placeholder="Godown Name"
        value={name}
        onChangeText={setName}
      />

      <TextInput
        style={styles.input}
        placeholder="Location"
        value={location}
        onChangeText={setLocation}
      />

      <Button title="Save Godown" onPress={handleSubmit} color="#007bff" />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 20, backgroundColor: '#fff' },
  header: { fontSize: 22, fontWeight: 'bold', marginBottom: 20, textAlign: 'center' },
  input: { borderWidth: 1, borderColor: '#ccc', padding: 10, marginBottom: 15, borderRadius: 5 }
});
