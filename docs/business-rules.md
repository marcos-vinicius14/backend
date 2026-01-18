# Business Rules

## Créditos (Cash Balance)
1. **Disponibilidade**: Apenas consome crédito se `credits_balance > 0`; caso contrário, lançar exceção de domínio.
2. **Sem saldo negativo**: Débitos não podem exceder o saldo; jamais permitir saldo negativo.
3. **Reembolso**: Em erros de processamento, permitir estorno (crédito de volta) mesmo sem transação Stripe associada no momento.

## Identidade e Pagamento
4. **Stripe elegível**: Só iniciar compra/checkout se houver `stripe_customer_id` válido; bloquear sessões de checkout sem essa associação.
5. **Integridade de perfil**: `full_name` e `email` não podem ser nulos/vazios; rejeitar atualizações inválidas para garantir dados mínimos para otimização.
