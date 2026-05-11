/**
 * Loan Calculator — Equal Installment / Equal Principal
 */

/* ============================================================
   Equal Installment (等额本息)
   ============================================================ */

function calcEqualInstallment(principal, months, annualRate) {
  const monthlyRate = annualRate / 12;
  const monthlyPayment =
    (principal * monthlyRate * Math.pow(1 + monthlyRate, months)) /
    (Math.pow(1 + monthlyRate, months) - 1);

  let remaining = principal;
  const schedule = [];

  for (let i = 1; i <= months; i++) {
    const interest = remaining * monthlyRate;
    const principalPaid = monthlyPayment - interest;
    remaining -= principalPaid;

    schedule.push({
      period: i,
      principal: round(principalPaid),
      interest: round(interest),
      payment: round(monthlyPayment),
      remaining: round(Math.max(0, remaining)),
    });
  }

  const totalPayment = monthlyPayment * months;
  const totalInterest = totalPayment - principal;

  return {
    monthlyPayment: round(monthlyPayment),
    totalInterest: round(totalInterest),
    totalPayment: round(totalPayment),
    schedule,
  };
}

/* ============================================================
   Equal Principal (等额本金)
   ============================================================ */

function calcEqualPrincipal(principal, months, annualRate) {
  const monthlyRate = annualRate / 12;
  const monthlyPrincipal = principal / months;

  let remaining = principal;
  let totalInterest = 0;
  const schedule = [];

  for (let i = 1; i <= months; i++) {
    const interest = remaining * monthlyRate;
    const payment = monthlyPrincipal + interest;
    remaining -= monthlyPrincipal;
    totalInterest += interest;

    schedule.push({
      period: i,
      principal: round(monthlyPrincipal),
      interest: round(interest),
      payment: round(payment),
      remaining: round(Math.max(0, remaining)),
    });
  }

  return {
    monthlyPayment: round(schedule[0].payment),
    totalInterest: round(totalInterest),
    totalPayment: round(principal + totalInterest),
    schedule,
  };
}

/* ============================================================
   Main Calculator
   ============================================================ */

function calculate(amount, term, rate, mode) {
  if (mode === 'EQUAL_PRINCIPAL') {
    return calcEqualPrincipal(amount, term, rate);
  }
  return calcEqualInstallment(amount, term, rate);
}

function round(val) {
  return Math.round(val * 100) / 100;
}
