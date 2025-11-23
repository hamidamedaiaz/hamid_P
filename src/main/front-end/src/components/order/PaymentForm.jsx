import { useState } from 'react';

const PaymentForm = ({ amount, onPaymentSubmit, loading }) => {
    const [paymentMethod, setPaymentMethod] = useState('CARD'); // 'CARD' or 'STUDENT_CREDIT'

    // État pour paiement par carte
    const [cardNumber, setCardNumber] = useState('');
    const [cardName, setCardName] = useState('');
    const [expiryDate, setExpiryDate] = useState('');
    const [cvv, setCvv] = useState('');

    // État pour crédit étudiant
    const [studentId, setStudentId] = useState('');
    const [studentEmail, setStudentEmail] = useState('');

    const handleSubmit = (e) => {
        e.preventDefault();

        let paymentData;

        if (paymentMethod === 'CARD') {
            // Validation pour carte bancaire
            if (!cardNumber || !cardName || !expiryDate || !cvv) {
                alert('Veuillez remplir tous les champs de la carte');
                return;
            }

            paymentData = {
                method: 'CARD',
                amount: amount,
                cardNumber: cardNumber.slice(-4),
                cardName: cardName
            };
        } else {
            // Validation pour crédit étudiant
            if (!studentId || !studentEmail) {
                alert('Veuillez remplir votre numéro étudiant et email');
                return;
            }

            paymentData = {
                method: 'STUDENT_CREDIT',
                amount: amount,
                studentId: studentId,
                studentEmail: studentEmail
            };
        }

        onPaymentSubmit(paymentData);
    };

    const formatCardNumber = (value) => {
        const cleaned = value.replace(/\s/g, '');
        const formatted = cleaned.match(/.{1,4}/g)?.join(' ') || cleaned;
        return formatted.slice(0, 19); // Max 16 chiffres + 3 espaces
    };

    const formatExpiryDate = (value) => {
        const cleaned = value.replace(/\D/g, '');
        if (cleaned.length >= 2) {
            return cleaned.slice(0, 2) + '/' + cleaned.slice(2, 4);
        }
        return cleaned;
    };

    return (
        <div className="payment-form" style={{
            maxWidth: '500px',
            margin: '20px auto',
            padding: '25px',
            border: '1px solid #ddd',
            borderRadius: '10px',
            backgroundColor: '#f9f9f9'
        }}>
            <div style={{
                textAlign: 'center',
                marginBottom: '20px',
                padding: '10px',
                backgroundColor: '#4CAF50',
                color: 'white',
                borderRadius: '5px'
            }}>
                <p style={{ margin: 0, fontSize: '14px' }}> Paiement sécurisé (simulation)</p>
            </div>

            <h3 style={{ marginBottom: '20px', fontSize: '20px', textAlign: 'center' }}>
                Montant à payer : <strong>{amount.toFixed(2)} €</strong>
            </h3>

            {/* Onglets de sélection du mode de paiement */}
            <div style={{ marginBottom: '25px' }}>
                <div style={{
                    display: 'flex',
                    gap: '10px',
                    borderBottom: '2px solid #ddd'
                }}>
                    <button
                        type="button"
                        onClick={() => setPaymentMethod('CARD')}
                        style={{
                            flex: 1,
                            padding: '12px',
                            border: 'none',
                            borderBottom: paymentMethod === 'CARD' ? '3px solid #FF6B35' : '3px solid transparent',
                            backgroundColor: paymentMethod === 'CARD' ? '#FFF5F2' : 'transparent',
                            cursor: 'pointer',
                            fontSize: '16px',
                            fontWeight: paymentMethod === 'CARD' ? 'bold' : 'normal',
                            transition: 'all 0.3s ease',
                            color: paymentMethod === 'CARD' ? '#FF6B35' : '#666'
                        }}
                    >
                        Carte Bancaire
                    </button>
                    <button
                        type="button"
                        onClick={() => setPaymentMethod('STUDENT_CREDIT')}
                        style={{
                            flex: 1,
                            padding: '12px',
                            border: 'none',
                            borderBottom: paymentMethod === 'STUDENT_CREDIT' ? '3px solid #FF6B35' : '3px solid transparent',
                            backgroundColor: paymentMethod === 'STUDENT_CREDIT' ? '#FFF5F2' : 'transparent',
                            cursor: 'pointer',
                            fontSize: '16px',
                            fontWeight: paymentMethod === 'STUDENT_CREDIT' ? 'bold' : 'normal',
                            transition: 'all 0.3s ease',
                            color: paymentMethod === 'STUDENT_CREDIT' ? '#FF6B35' : '#666'
                        }}
                    >
                        🎓 Crédit Étudiant
                    </button>
                </div>
            </div>

            <form onSubmit={handleSubmit}>
                {/* Formulaire Carte Bancaire */}
                {paymentMethod === 'CARD' && (
                    <>
                        <div style={{ marginBottom: '15px' }}>
                            <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>
                                Numéro de carte
                            </label>
                            <input
                                type="text"
                                value={cardNumber}
                                onChange={(e) => setCardNumber(formatCardNumber(e.target.value))}
                                placeholder="1234 5678 9012 3456"
                                maxLength="19"
                                style={{
                                    width: '100%',
                                    padding: '10px',
                                    fontSize: '16px',
                                    border: '1px solid #ddd',
                                    borderRadius: '5px',
                                    boxSizing: 'border-box'
                                }}
                            />
                        </div>

                        <div style={{ marginBottom: '15px' }}>
                            <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>
                                Nom du titulaire
                            </label>
                            <input
                                type="text"
                                value={cardName}
                                onChange={(e) => setCardName(e.target.value.toUpperCase())}
                                placeholder="JEAN DUPONT"
                                style={{
                                    width: '100%',
                                    padding: '10px',
                                    fontSize: '16px',
                                    border: '1px solid #ddd',
                                    borderRadius: '5px',
                                    boxSizing: 'border-box'
                                }}
                            />
                        </div>

                        <div style={{ display: 'flex', gap: '15px', marginBottom: '20px' }}>
                            <div style={{ flex: 1 }}>
                                <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>
                                    Date d'expiration
                                </label>
                                <input
                                    type="text"
                                    value={expiryDate}
                                    onChange={(e) => setExpiryDate(formatExpiryDate(e.target.value))}
                                    placeholder="MM/AA"
                                    maxLength="5"
                                    style={{
                                        width: '100%',
                                        padding: '10px',
                                        fontSize: '16px',
                                        border: '1px solid #ddd',
                                        borderRadius: '5px',
                                        boxSizing: 'border-box'
                                    }}
                                />
                            </div>

                            <div style={{ flex: 1 }}>
                                <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>
                                    CVV
                                </label>
                                <input
                                    type="text"
                                    value={cvv}
                                    onChange={(e) => setCvv(e.target.value.replace(/\D/g, '').slice(0, 3))}
                                    placeholder="123"
                                    maxLength="3"
                                    style={{
                                        width: '100%',
                                        padding: '10px',
                                        fontSize: '16px',
                                        border: '1px solid #ddd',
                                        borderRadius: '5px',
                                        boxSizing: 'border-box'
                                    }}
                                />
                            </div>
                        </div>
                    </>
                )}

                {/* Formulaire Crédit Étudiant */}
                {paymentMethod === 'STUDENT_CREDIT' && (
                    <>
                        <div style={{
                            backgroundColor: '#e3f2fd',
                            padding: '15px',
                            borderRadius: '5px',
                            marginBottom: '20px',
                            fontSize: '14px',
                            color: '#1976d2'
                        }}>
                             Utilisez votre crédit étudiant pour payer cette commande.
                            Le montant sera déduit de votre compte étudiant.
                        </div>

                        <div style={{ marginBottom: '15px' }}>
                            <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>
                                Numéro étudiant
                            </label>
                            <input
                                type="text"
                                value={studentId}
                                onChange={(e) => setStudentId(e.target.value)}
                                placeholder="ex: 21234567"
                                style={{
                                    width: '100%',
                                    padding: '10px',
                                    fontSize: '16px',
                                    border: '1px solid #ddd',
                                    borderRadius: '5px',
                                    boxSizing: 'border-box'
                                }}
                            />
                        </div>

                        <div style={{ marginBottom: '20px' }}>
                            <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>
                                Email universitaire
                            </label>
                            <input
                                type="email"
                                value={studentEmail}
                                onChange={(e) => setStudentEmail(e.target.value)}
                                placeholder="prenom.nom@etu.unice.fr"
                                style={{
                                    width: '100%',
                                    padding: '10px',
                                    fontSize: '16px',
                                    border: '1px solid #ddd',
                                    borderRadius: '5px',
                                    boxSizing: 'border-box'
                                }}
                            />
                        </div>

                        <div style={{
                            backgroundColor: '#fff3cd',
                            padding: '12px',
                            borderRadius: '5px',
                            marginBottom: '20px',
                            fontSize: '13px',
                            color: '#856404',
                            border: '1px solid #ffc107'
                        }}>
                            Solde disponible : <strong>150.00 €</strong> (simulation)
                        </div>
                    </>
                )}

                <button
                    type="submit"
                    disabled={loading}
                    style={{
                        width: '100%',
                        padding: '15px',
                        backgroundColor: loading ? '#ccc' : '#FF6B35',
                        color: 'white',
                        border: 'none',
                        borderRadius: '5px',
                        fontSize: '18px',
                        fontWeight: 'bold',
                        cursor: loading ? 'wait' : 'pointer',
                        transition: 'background-color 0.3s ease'
                    }}
                >
                    {loading ? ' Traitement en cours...' :
                     paymentMethod === 'CARD' ? ' Payer par carte' : 'Payer avec crédit étudiant'}
                </button>
            </form>

            <p style={{
                marginTop: '15px',
                textAlign: 'center',
                fontSize: '12px',
                color: '#666',
                fontStyle: 'italic'
            }}>
                Il s'agit d'un paiement simulé. Aucune transaction réelle ne sera effectuée.
            </p>
        </div>
    );
};

export default PaymentForm;

