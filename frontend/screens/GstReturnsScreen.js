import React, { useState, useContext } from 'react';
import { View, Text, Button, StyleSheet, Alert, Platform, ScrollView } from 'react-native';
import { AuthContext } from '../context/AuthContext';

const API_URL = Platform.OS === 'android' ? 'http://10.0.2.2:8080/api' : 'http://localhost:8080/api';

export default function GstReturnsScreen() {
  const { userToken } = useContext(AuthContext);
  const [gstJson, setGstJson] = useState('');
  const [loading, setLoading] = useState(false);

  const handleGenerate = async () => {
    setLoading(true);
    try {
      const response = await fetch(`${API_URL}/tax/gstr1/generate`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${userToken}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify([]) // In a real scenario, pass actual invoice data
      });

      if (response.ok) {
        const data = await response.json();
        setGstJson(JSON.stringify(data, null, 2));
        Alert.alert("Success", "GSTR-1 JSON Generated!");
      } else {
        Alert.alert("Error", "Failed to generate GSTR-1.");
      }
    } catch (error) {
      Alert.alert("Error", `Network error: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.header}>GST Returns (GSTR-1)</Text>

      <Button title="Generate GSTR-1 Offline JSON" onPress={handleGenerate} color="#28a745" />

      <ScrollView style={styles.jsonContainer}>
        <Text style={styles.jsonText}>
          {gstJson ? gstJson : 'Click generate to view offline JSON schema...'}
        </Text>
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 20, backgroundColor: '#fff' },
  header: { fontSize: 22, fontWeight: 'bold', marginBottom: 20, textAlign: 'center' },
  jsonContainer: { marginTop: 20, padding: 10, backgroundColor: '#f0f0f0', borderRadius: 5, flex: 1 },
  jsonText: { fontFamily: 'monospace', fontSize: 12 }
});
